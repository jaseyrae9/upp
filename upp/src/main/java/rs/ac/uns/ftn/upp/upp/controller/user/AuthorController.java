package rs.ac.uns.ftn.upp.upp.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.ac.uns.ftn.upp.upp.dto.FormFieldsDTO;
import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.dto.TaskDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.model.user.Coauthor;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.camunda.HelperService;

@Controller
@RequestMapping("/author")
public class AuthorController {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private HelperService helperService;

	/**
	 * Uzimamo prvi user task iz bpmn modela i polja forme tog user taska, zatim ta
	 * polja saljemo frontu.
	 * 
	 * @return formfield dto za front
	 */
	@PreAuthorize("hasAnyRole('AUTHOR')")
	@GetMapping(path = "/get", produces = "application/json")
	public @ResponseBody FormFieldsDTO get(Authentication authentication) {
		System.err.println("U kontroleru za slanje forme za dodavanje rada");
		// System.out.println("IME POKRETACA PROCESA: " + authentication.getName());
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("ObradaPodnetogTeksta");
		
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);
		// runtimeService.setVariable(pi.getId(), "pokretacAutor", authentication.getName());
		
		TaskFormData tfd = formService.getTaskFormData(task.getId());
		List<FormField> properties = tfd.getFormFields();
		for (FormField fp : properties) {
			System.out.println(fp.getId() + fp.getType());
		}

		runtimeService.setVariable(pi.getId(), "istekli", new HashSet<Customer>());
		runtimeService.setVariable(pi.getId(), "coauthors", new HashSet<Coauthor>());

		return new FormFieldsDTO(task.getId(), pi.getId(), properties);
	}

	/**
	 * Odabir casopisa endpoint, complituje se user task za izbor, klikom na dugme
	 * iz forme za izbor casopisa, da bi nastavili dalje.
	 * 
	 * @param dto    podaci iz forme
	 * @param taskId id taska koji se submituje
	 * @return
	 */
	@PreAuthorize("hasAnyRole('AUTHOR')")
	@PostMapping(path = "/post/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> post(@RequestBody List<FormSubmissionDTO> dto, @PathVariable String taskId) {
		System.err.println("odabir casopisa u koji zelimo da dodamo rad ");

		HashMap<String, Object> map = helperService.mapListToDto(dto);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null
																					// ili task sa tim id-om
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("task name: " + task.getName());
		System.err.println("processInstanceId: " + processInstanceId.toString());

		runtimeService.setVariable(processInstanceId, "dto", dto); // u dto variablu smo stavili dto
		formService.submitTaskForm(taskId, map);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Prikazuje taskove koji su dodeljeni ulogovanoj osobi
	 * 
	 * @param processInstanceId
	 * @return
	 * @throws RequestDataException
	 */
	@GetMapping(path = "/tasks", produces = "application/json")
	public @ResponseBody ResponseEntity<List<TaskDTO>> getTasks(Authentication authentication)
			throws RequestDataException {
		System.err.println("AUTOR: GET /tasks");
		System.out.println("IME POKRETACA PROCESA: " + authentication.getName());

		List<Task> tasks = taskService.createTaskQuery().taskAssignee(authentication.getName()).list();

		System.err.println("task.size() " + tasks.size());
		List<TaskDTO> dtos = new ArrayList<TaskDTO>();
		for (Task task : tasks) {
			TaskDTO t = new TaskDTO(task.getId(), task.getName(), task.getAssignee());
			dtos.add(t);
		}
		return new ResponseEntity<List<TaskDTO>>(dtos, HttpStatus.OK);

	}

	/**
	 * Prikazuje formu za trazeni task.
	 * 
	 * @param taskId - id taska
	 * @return
	 */
	@GetMapping(path = "/fields/{taskId}", produces = "application/json")
	public @ResponseBody FormFieldsDTO getFields(@PathVariable String taskId, Authentication authentication) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

		if (task.getAssignee().equals(authentication.getName())) {
			// za taj task daj mi formu
			TaskFormData tfd = formService.getTaskFormData(task.getId());
			// lista form fildova i ispise u konzoli
			List<FormField> properties = tfd.getFormFields();

			for (FormField fp : properties) {
				System.out.println(fp.getId() + fp.getType());
				if (fp.getId().contentEquals("ponovoIzaberi") || fp.getId().contentEquals("ponovoIzaberiRecenzenta")) {
					Integer paperId = Integer
							.parseInt(String.valueOf(runtimeService.getVariable(task.getProcessInstanceId(), "radId")));
					Set<Customer> istekli = (HashSet<Customer>) runtimeService.getVariable(task.getProcessInstanceId(),
							"istekli");

					System.out.println("ID rada za koga se preuzimaju polja: " + paperId);
					helperService.fillReviewers(properties, paperId, istekli);
				} 
//				if(fp.getId().contentEquals("ponovoIzaberiRecenzenta")) {
//					Integer paperId = Integer
//							.parseInt(String.valueOf(runtimeService.getVariable(task.getProcessInstanceId(), "radId")));
//					Set<Customer> istekli = (HashSet<Customer>) runtimeService.getVariable(task.getProcessInstanceId(),
//							"istekli");
//
//					System.out.println("ID rada za koga se preuzimaju polja: " + paperId);
//					helperService.fillReviewers(properties, paperId, istekli);
//				}
				
			}

			return new FormFieldsDTO(task.getId(), task.getProcessInstanceId(), properties);
		}

		return null;

	}

	/**
	 * Submit formom se complete-uje zadatak i omogucava nastavak procesa.
	 * 
	 * @param dto    podaci
	 * @param taskId id taska koji se submituje
	 * @return
	 * @throws RequestDataException
	 */
	@PreAuthorize("hasAnyRole('AUTHOR') or hasAnyRole('EDITORINCHIEF')")
	@PostMapping(path = "/submit/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> submit(@RequestBody List<FormSubmissionDTO> dto, @PathVariable String taskId,
			Authentication authentication) throws RequestDataException {
		System.err.println("submit endpoint: ");

		// Proveravamo da li je izabrao barem 2 recenzenta
		for (FormSubmissionDTO temp : dto) {
			if (temp.getFieldId().contentEquals("recenzenti")) {
				System.out.println("DEBUG: Naisli smo na listu recenzenata");
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if (listTemp.size() < 2) {
					System.out.println("DEBUG: Nisu izabrana bar 2 recenzenta");
					throw new RequestDataException("Morate izabrati makar dva recenzenta.");
				}
			}
		}

		HashMap<String, Object> map = helperService.mapListToDto(dto);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null
																					// ili task sa tim id-om
		System.err.println(" submit name: " + task.getName());
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("submit->processInstanceId: " + processInstanceId.toString());

		runtimeService.setVariable(processInstanceId, "submit", dto); // u submit variablu smo stavili dto
		runtimeService.setVariable(processInstanceId, "korisnik", authentication.getName()); // onaj ko submituje
		formService.submitTaskForm(taskId, map);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
