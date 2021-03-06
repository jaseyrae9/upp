package rs.ac.uns.ftn.upp.upp.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.ac.uns.ftn.upp.upp.dto.FormFieldsDTO;
import rs.ac.uns.ftn.upp.upp.dto.TaskDTO;
import rs.ac.uns.ftn.upp.upp.dto.user.UserDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private JournalService journalService;
	
	@Autowired
	private CustomerService customerService;

	@Autowired
	private AuthorityService authorityService;
	
	/**
	 *  Returns users. Objects contain username, first name, last name and authority.
	 *  users who already play the role of admin and editor should not be shown
	 *  Users who already have the admin and editor authority shoudn't be returned.
	 *  
	 * @return information about users.
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> getJournals() {
		System.err.println("Usao u get users controller");
		Set<UserDTO> ret = customerService.getUsers();
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value = "/makeAdmin/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> makeAdmin(@PathVariable Integer id) throws NotFoundException, RequestDataException {
		System.err.println("Usao u makeAdmin controller");
		Customer ret = customerService.makeAdmin(id);
		return  new ResponseEntity<>(new UserDTO(ret), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value = "/makeEditor/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> makeEditor(@PathVariable Integer id) throws NotFoundException, RequestDataException {
		System.err.println("Usao u makeEditor controller");
		Customer ret = customerService.makeEditor(id);
		return  new ResponseEntity<>(new UserDTO(ret), HttpStatus.OK);
	}
	
	/**
	 * Prikazuje taskove koji su dodeljeni adminu, tj. demo korisniku.
	 * 
	 * @param processInstanceId
	 * @return
	 */
	@GetMapping(path = "/tasks", produces = "application/json")
	public @ResponseBody ResponseEntity<List<TaskDTO>> getTasks() {
		System.err.println("DEBUG: GET /tasks");
		List<Task> tasks = taskService.createTaskQuery().taskAssignee("demo").list();
		System.err.println("task.size() " + tasks.size());
		List<TaskDTO> dtos = new ArrayList<TaskDTO>();
		for (Task task : tasks) {
			TaskDTO t = new TaskDTO(task.getId(), task.getName(), task.getAssignee());
			dtos.add(t);
		}

		return new ResponseEntity(dtos, HttpStatus.OK);
	}
	
	/**
	 * Prikazuje formu adminu gde on donosi odluku.
	 * 
	 * @param taskId
	 * @return
	 */
	@GetMapping(path = "/fields/{taskId}", produces = "application/json")
	public @ResponseBody FormFieldsDTO getFields(@PathVariable String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

		if (task.getAssignee().equals("demo")) {
			// za taj task daj mi formu
			TaskFormData tfd = formService.getTaskFormData(task.getId());
			// lista form fildova i ispise u konzoli
			List<FormField> properties = tfd.getFormFields();
						
			Integer journalId = (Integer) runtimeService.getVariable(task.getProcessInstanceId(), "idCasopis");
			System.out.println("ID Casopisa za koga se preuzimaju polja: " + journalId);
			
			if(journalId != null) {
				// Popunjavanje podacima
				fillChoosenData(properties, journalId);
			}
			
			for (FormField fp : properties) {
				System.out.println(fp.getId() + fp.getType());
			}
			
			return new FormFieldsDTO(task.getId(), task.getProcessInstanceId(), properties);
		}

		return null;

	}

	private void fillChoosenData(List<FormField> properties, Integer journalId) {
		Optional<Journal> opt = journalService.findById(journalId);
		
		if(!opt.isPresent()) {
			System.err.println("Nije pronadjen casopis ID: " + journalId);
			return; // Necemo nista popuniti
		}
		
		Journal journal = opt.get();
		
		Map<String, String> items = new HashMap<>();
		Map<String, String> itemsEditors = new HashMap<>();
		Map<String, String> itemsReviewers = new HashMap<>();
		
		if(!properties.isEmpty()) {
			for(FormField field : properties) {
				System.err.println("field " + field.getId());
				if(field.getId().equals("izabraneNaucneOblasti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					items = eft.getValues();
					items.clear(); // Praznimo mapu ako ima stari vrednosti
					
					for(AcademicField academicField: journal.getJournalAcademicFields()) {
						System.out.println("dodato polje: " + academicField.getId() + " vrednost: " + academicField.getName());
						items.put(academicField.getId().toString(), academicField.getName());
						
					}
				}
				if(field.getId().equals("izabraniUrednici")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemsEditors = eft.getValues();
					itemsEditors.clear(); 
					for(Customer customer: journal.getEditors()) {
						System.out.println("itemsEditors dodato polje: " + customer.getId() + " vrednost: " + customer.getUsername());
						itemsEditors.put(customer.getId().toString(), customer.getUsername());
					}
				}
				if(field.getId().equals("izabraniRecenzenti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemsReviewers = eft.getValues();
					itemsReviewers.clear();
					for(Customer customer: journal.getJournalReviewers()) {
						System.out.println("itemsReviewers dodato polje: " + customer.getId() + " vrednost: " + customer.getUsername());
						itemsReviewers.put(customer.getId().toString(), customer.getUsername());
					}
				}
				
			}
		}		
		
	}


}
