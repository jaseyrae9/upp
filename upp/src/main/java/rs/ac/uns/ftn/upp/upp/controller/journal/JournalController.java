package rs.ac.uns.ftn.upp.upp.controller.journal;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.ac.uns.ftn.upp.upp.dto.FormFieldsDTO;
import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.AuthenticationResponse;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.security.auth.TokenUtils;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.CustomUserDetailsService;

@Controller
@RequestMapping("/journal")
public class JournalController {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private AcademicFieldService academicFieldService;

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private TokenUtils tokenUtils;
	
	/**
	 * Uzimamo prvi user task iz bpmn modela i polja forme tog user taska, zatim ta
	 * polja saljemo frontu.
	 * 
	 * @return formfield dto za front
	 */
	// saljemo formu za user task na front -> kad se klikne dugme u navbaru dodaj
	// casopis
	@PreAuthorize("hasAnyRole('EDITOR')")
	@GetMapping(path = "/get", produces = "application/json")
	public @ResponseBody FormFieldsDTO get(Authentication authentication) {
		System.err.println("U kontroleru za slanje forme za dodavanje casopisa");
		System.out.println("IME POKRETACA PROCESA: " + authentication.getName());

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("dodavanjeCasopisa");
		System.err.println("casopis pii: " + pi.getProcessInstanceId());
		// uzimamo prvi task
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);
		runtimeService.setVariable(pi.getId(), "pokretac", authentication.getName());
		task.setAssignee(authentication.getName());
		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(task.getId());

		// lista form fildova i ispise u konzoli
		List<FormField> properties = tfd.getFormFields();
		for (FormField fp : properties) {
			System.out.println(fp.getId() + fp.getType());
		}

		// pravi dto u koji smesti taj task id, process instance id i te propertis
		// odnosno te form fieldove i to se salje na front
		return new FormFieldsDTO(task.getId(), pi.getId(), properties);
	}

	// dodavanje casopisa endpoint - completujemo user task za unos podataka da bi
	// nastavili dalje
	// -> kad se klikne dugme iz forme za dodavanje
	// salju nam se podaci sa fronta
	@PreAuthorize("hasAnyRole('EDITOR')")
	@PostMapping(path = "/post/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> createJournal(@RequestBody List<FormSubmissionDTO> dto, @PathVariable String taskId,
			Authentication authentication, Device device) {
		System.err.println("dodavanje casopisa endpoiny: ");

		HashMap<String, Object> map = this.mapListToDto(dto);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null
																					// ili task sa tim id-om
		System.err.println("task name: " + task.getName());
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("processInstanceId: " + processInstanceId.toString());

		String starter = authentication.getName();
		Optional<Customer> opt = customerService.findCustomer(starter);
		if (!opt.isPresent()) {
			// TODO: exception
			System.err.println("[addJournalService] nemaa ga");
		}
		Customer cust = opt.get();
		
		if (cust.getJournal() != null) {
			System.out.println("Ovaj urednik vec ima casopis");
		} else {
			// ocemo da postavimo procesnu variablu bas na taj proces instance id iz kojeg
			// je taj task protekao
			// za taj process instance
			// registration se zove variabla
			// stavi taj dto koji je korisnik poslao
			runtimeService.setVariable(processInstanceId, "casopis", dto); // u casopis variablu smo stavili dto

			// submituj task form
			formService.submitTaskForm(taskId, map);

		}

		// Kreiraj token
		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(cust.getUsername());
		String token = this.tokenUtils.generateToken(userDetails, device);

		// Vrati token kao odgovor na uspesno autentifikaciju
		return ResponseEntity.ok(new AuthenticationResponse(token));
	}

	/**
	 * Uzimamo prvi user task iz bpmn modela i polja forme tog user taska, zatim ta
	 * polja saljemo frontu.
	 * 
	 * @return formfield dto za front
	 */
	// saljemo formu za user task na front -> kad se klikne dugme u navbaru za
	// odabir urednika i recenzenata
	@PreAuthorize("hasAnyRole('EDITOR')")
	@GetMapping(path = "/getForm", produces = "application/json")
	public @ResponseBody FormFieldsDTO getSecondUserTaskForm(Authentication authentication) {
		System.err.println("U kontroleru za slanje forme za odabir recenzenata i urednika");
		System.out.println("2IME POKRETACA PROCESA: " + authentication.getName());

		Task task = taskService.createTaskQuery().taskAssignee(authentication.getName()).singleResult();
		
		
		if(!task.getName().trim().contentEquals("dodavanje uredjivackog odbora")) {
			System.out.println("GRESKA: Trenutno nema taska za dodavanje urednika i recezenata");
			return null;
		}
		System.out.println("Task name: " + task.getName());
		System.out.println("Task id: " + task.getId());
		System.err.println("pokretacc variabla: " + runtimeService.getVariable(task.getProcessInstanceId(), "pokretac"));

		TaskFormData tfd = formService.getTaskFormData(task.getId());

		// lista form fildova i ispise u konzoli
		List<FormField> properties = tfd.getFormFields();
		return new FormFieldsDTO(task.getId(), task.getProcessInstanceId(), properties);
	}
	
	@PreAuthorize("hasAnyRole('EDITOR')")
	@GetMapping(path = "/getEditJournalForm", produces = "application/json")
	public @ResponseBody FormFieldsDTO getEditJournalForm(Authentication authentication) {
		System.err.println("DEBUG: U kontroleru getEditJournalForm");
		Task task = taskService.createTaskQuery().taskAssignee(authentication.getName()).singleResult();
		
		
		if(!task.getName().trim().contentEquals("editor menja podatke")) {
			System.out.println("GRESKA: Trenutno nema taska za promenu podataka casopisa");
			return null;
		}
		System.out.println("Task name: " + task.getName());
		System.out.println("Task id: " + task.getId());
		System.err.println("pokretacc variabla: " + runtimeService.getVariable(task.getProcessInstanceId(), "pokretac"));

		TaskFormData tfd = formService.getTaskFormData(task.getId());
		List<FormField> properties = tfd.getFormFields();
		
		return new FormFieldsDTO(task.getId(), task.getProcessInstanceId(), properties);
	}

	// dodavanje casopisa endpoint - completujemo user task za unos podataka da bi
	// nastavili dalje
	// -> kad se klikne dugme iz forme za dodavanje
	// salju nam se podaci sa fronta
	@PreAuthorize("hasAnyRole('EDITOR')")
	@PostMapping(path = "/addEditorsAndReviewers/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> addEditorsAndReviewers(@RequestBody List<FormSubmissionDTO> dto,
			@PathVariable String taskId, Authentication authentication) {
		System.err.println("addEditorsAndReviewers endpoint: ");

		// Proveravamo da li je izabrao barem 2 recenzenta
		for (FormSubmissionDTO temp : dto) {
			if(temp.getFieldId().contentEquals("recenzentiNaucnihOblasti")) {
				System.out.println("DEBUG: Naisli smo na listu recenzenata");
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if(listTemp.size() < 2) {
					System.out.println("DEBUG: Nisu izabrana bar 2 recenzenta");
					// TODO: error
				}
			}
		}
		
		HashMap<String, Object> map = this.mapListToDto2(dto);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null
																					// ili task sa tim id-om
		System.err.println("task name: " + task.getName());
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("processInstanceId: " + processInstanceId.toString());

		runtimeService.setVariable(processInstanceId, "uredniciIrecenzenti", dto); // u casopis variablu smo stavili dto

		// submituj task form
		System.out.println(map);
		formService.submitTaskForm(taskId, map);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('EDITOR')")
	@PostMapping(path = "/editJournal/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> editJournal(@RequestBody List<FormSubmissionDTO> dto, @PathVariable String taskId, Authentication authentication) {
		System.err.println("editJournal endpoint: ");

		// Proveravamo da li je izabrao barem 2 recenzenta
		/* for (FormSubmissionDTO temp : dto) {
			if(temp.getFieldId().contentEquals("recenzentiNaucnihOblasti")) {
				System.out.println("DEBUG: Naisli smo na listu recenzenata");
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if(listTemp.size() < 2) {
					System.out.println("DEBUG: Nisu izabrana bar 2 recenzenta");
					// TODO: error
				}
			}
		}*/
		for (FormSubmissionDTO temp : dto) {
			System.out.println();
		}
		
		HashMap<String, Object> map = this.mapListToDto(dto);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null
																					// ili task sa tim id-om
		System.err.println("task name: " + task.getName());
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("processInstanceId: " + processInstanceId.toString());

	
		runtimeService.setVariable(processInstanceId, "izmenjenCasopis", dto); // u casopis variablu smo stavili dto

		// submituj task form
		System.out.println(map);
		formService.submitTaskForm(taskId, map);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private HashMap<String, Object> mapListToDto2(List<FormSubmissionDTO> list) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (FormSubmissionDTO temp : list) {
			System.err.println("sve " + temp.getFieldId());

			if (temp.getFieldValue() instanceof List) {
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if (!listTemp.isEmpty()) {
					System.err.println("LSITA " + temp.getFieldId());

					Customer c = customerService.findByUsername(listTemp.get(0));
					map.put(temp.getFieldId(), c.getId().toString());
				}
			} else {
				map.put(temp.getFieldId(), temp.getFieldValue());
			}
		}

		return map;
	}

	private HashMap<String, Object> mapListToDto(List<FormSubmissionDTO> list) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (FormSubmissionDTO temp : list) {
			if (temp.getFieldValue() instanceof List) {
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if (!listTemp.isEmpty()) {
					AcademicField af = academicFieldService.findByName(listTemp.get(0));
					map.put(temp.getFieldId(), af.getId().toString());
				}
			} else {
				map.put(temp.getFieldId(), temp.getFieldValue());
			}
		}

		return map;
	}
}
