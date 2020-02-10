package rs.ac.uns.ftn.upp.upp.controller.journal;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.ac.uns.ftn.upp.upp.dto.FormFieldsDTO;
import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.dto.journal.JournalDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.exceptions.ResourceNotFoundException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.AuthenticationResponse;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.security.auth.TokenUtils;
import rs.ac.uns.ftn.upp.upp.service.camunda.HelperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
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
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private JournalService journalService;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private HelperService helperService;
	
	private final static String EDITORS_GROUP_ID = "editors";
	
	/**
	 *  Returns active objects journals. Objects contain id and name.
	 *  
	 * @return information about active journals.
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> getJournals() {
		System.err.println("Usao u get all journals controler");
		Set<JournalDTO> ret = journalService.getJournals();
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}
	
	/**
	 * Returns data about journal selected id.
	 * 
	 * @param id - id of journal
	 * @return
	 * @throws NotFoundException 
	 * @throws ResourceNotFoundException - if there is no journal with selected id
	 */
	@RequestMapping(value = "/getJournal/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getJournal(@PathVariable Integer id) throws NotFoundException {
		System.err.println("contoler get journal sa id " + id);
		JournalDTO journal = journalService.getJournal(id);	
		return new ResponseEntity<>(journal, HttpStatus.OK);
	}

	/**
	 * Uzimamo prvi user task iz bpmn modela i polja forme tog user taska, zatim ta
	 * polja saljemo frontu.
	 * 
	 * @return formfield dto za front
	 * @throws RequestDataException 
	 */
	// @PreAuthorize("hasAnyRole('EDITOR')")
	@GetMapping(path = "/get", produces = "application/json")
	public @ResponseBody FormFieldsDTO get(Authentication authentication) throws RequestDataException {
		System.err.println("U kontroleru za slanje forme za dodavanje casopisa");
		String pokretac = identityService.getCurrentAuthentication().getUserId();
		System.out.println("IME POKRETACA PROCESA: " + pokretac);

		boolean authorized = helperService.authorize(EDITORS_GROUP_ID);
		if(!authorized) {
			throw new RequestDataException("Nemate odgovarajucu ulogu");
		}
		
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("dodavanjeCasopisa");
		System.err.println("casopis pii: " + pi.getProcessInstanceId());

		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);

		// postavljam onog ko je pokrenuo sistem u variablu pokretac kako bi se naredni
		// taskovi koji kao assignee imaju variablu pokretac zaista dodelili tom
		// korisniku
		//runtimeService.setVariable(pi.getId(), "pokretac", authentication.getName());
		//task.setAssignee(authentication.getName());
		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(task.getId());

		// lista form fildova i ispise u konzoli
		List<FormField> properties = tfd.getFormFields();
		for (FormField fp : properties) {
			System.out.println(fp.getId() + fp.getType());
		}

		// pravi dto u koji smesti taj task id, process instance id i te propertis
		return new FormFieldsDTO(task.getId(), pi.getId(), properties);
	}

	/**
	 * Dodavanje casopisa. Nakon submit-a forme se complete-uje zadatak sto
	 * omogucava nastavak procesa.
	 * 
	 * @param dto            podaci
	 * @param taskId         id taska koji se complete-uje
	 * @param authentication korisnik koji je pozvao endpoint
	 * @param device
	 * @return
	 * @throws NotFoundException
	 * @throws RequestDataException
	 */
	@PreAuthorize("hasAnyRole('EDITOR')")
	@PostMapping(path = "/post/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> createJournal(@RequestBody List<FormSubmissionDTO> dto,
			@PathVariable String taskId, Authentication authentication, Device device)
			throws NotFoundException, RequestDataException {
		System.err.println("dodavanje casopisa endpoint: ");

		boolean authorized = helperService.authorize(EDITORS_GROUP_ID);
		if(!authorized) {
			throw new RequestDataException("Nemate odgovarajucu ulogu");
		}
		
		HashMap<String, Object> map = this.mapListToDto(dto);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		System.err.println("task name: " + task.getName());

		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("processInstanceId: " + processInstanceId.toString());

		String starter = identityService.getCurrentAuthentication().getUserId();
		System.out.println("IME POKRETACA PROCESA: " + starter);

		//String starter = authentication.getName();
		Optional<Customer> opt = customerService.findCustomer(starter);
		if (!opt.isPresent()) {
			System.err.println("[addJournalService] nemaa korisnika imenom: " + starter);
			throw new NotFoundException(starter, Customer.class.getSimpleName());

		}
		Customer cust = opt.get();

		if (cust.getJournal() != null) {
			System.err.println("Ovaj urednik već ima časopis.");
			throw new RequestDataException("Ne možete da kreirate časopis jer već imate jedan časopis.");
		} else {
			runtimeService.setVariable(processInstanceId, "casopis", dto); // u casopis variablu smo stavili dto
			// submituj task form
			formService.submitTaskForm(taskId, map);
		}

		// da bi odmah dobio u tokenu ulogu da je glavni urednik
		// Kreiraj token
		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(cust.getUsername());
		String token = this.tokenUtils.generateToken(userDetails, device);
    	
		User user = identityService.createUserQuery().userId(cust.getUsername()).singleResult();
        identityService.setAuthenticatedUserId(user.getId());

		List<Group> groups =  identityService.createGroupQuery().groupMember(user.getId()).list();

		// Vrati token kao odgovor na uspesno autentifikaciju
		return ResponseEntity.ok(new AuthenticationResponse(token, groups));
	}

	/**
	 * Preuzimanje forme za dodavanje uredjivackog odbora.
	 * 
	 * 
	 * @return formfield dto za front
	 * @throws RequestDataException
	 */
	@PreAuthorize("hasAnyRole('EDITORINCHIEF')")
	@GetMapping(path = "/getForm", produces = "application/json")
	public @ResponseBody FormFieldsDTO getAddEditorsAndReviewersTaskForm(Authentication authentication)
			throws RequestDataException {
		System.err.println("U kontroleru za slanje forme za odabir recenzenata i urednika");
		// System.out.println("2IME POKRETACA PROCESA: " + authentication.getName());
		String starter = identityService.getCurrentAuthentication().getUserId();
		System.out.println("IME POKRETACA PROCESA: " + starter);
		
		Task task = taskService.createTaskQuery().taskAssignee(starter).singleResult();
		if (task == null) {
			System.out.println("GRESKA: null Trenutno nema taska za dodavanje urednika i recezenata");
			throw new RequestDataException("Trenutno nema taska za dodavanje urednika i recezenata.");
		}

		if (!task.getName().trim().contentEquals("dodavanje uredjivackog odbora")) {
			System.out.println("GRESKA: Trenutno nema taska za dodavanje urednika i recezenata");
			throw new RequestDataException("Trenutno nema taska za dodavanje urednika i recezenata.");
		}

		System.out.println("Task name: " + task.getName());
		System.out.println("Task id: " + task.getId());
		System.err.println("pokretacc variabla: " + runtimeService.getVariable(task.getProcessInstanceId(), "pokretac"));

		TaskFormData tfd = formService.getTaskFormData(task.getId());

		// lista form fildova i ispise u konzoli
		List<FormField> properties = tfd.getFormFields();
		return new FormFieldsDTO(task.getId(), task.getProcessInstanceId(), properties);
	}

	/**
	 * Preuzimanje forme za izmenu podataka o casopisu.
	 * 
	 * @param authentication
	 * @return
	 * @throws RequestDataException
	 */
	@PreAuthorize("hasAnyRole('EDITORINCHIEF')")
	@GetMapping(path = "/getEditJournalForm", produces = "application/json")
	public @ResponseBody FormFieldsDTO getEditJournalForm(Authentication authentication) throws RequestDataException {
		System.err.println("DEBUG: U kontroleru getEditJournalForm");
		String starter = identityService.getCurrentAuthentication().getUserId();
		System.out.println("IME POKRETACA PROCESA: " + starter);
		
		Task task = taskService.createTaskQuery().taskAssignee(starter).singleResult();

		if (task == null) {
			System.out.println("GRESKA: null Trenutno nema taska za promenu podataka casopisa");
			throw new RequestDataException("Trenutno nema taska za promenu podataka časopisa.");
		}

		if (!task.getName().trim().contentEquals("editor menja podatke")) {
			System.out.println("GRESKA: Trenutno nema taska za promenu podataka casopisa");
			throw new RequestDataException("Trenutno nema taska za promenu podataka časopisa.");
		}

		TaskFormData tfd = formService.getTaskFormData(task.getId());
		List<FormField> properties = tfd.getFormFields();

		return new FormFieldsDTO(task.getId(), task.getProcessInstanceId(), properties);
	}

	/**
	 * Dodavanje urednika i recenzenata naucnih oblasti casopisa end point. Nakon
	 * submit-a forme se complete-uje zadatak što omogućava nastavak procesa.
	 * 
	 * @param dto
	 * @param taskId
	 * @param authentication
	 * @return
	 * @throws RequestDataException
	 */
	@PreAuthorize("hasAnyRole('EDITORINCHIEF')")
	@PostMapping(path = "/addEditorsAndReviewers/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> addEditorsAndReviewers(@RequestBody List<FormSubmissionDTO> dto,
			@PathVariable String taskId, Authentication authentication) throws RequestDataException {
		System.err.println("addEditorsAndReviewers endpoint: ");

		// Proveravamo da li je izabrao barem 2 recenzenta
		for (FormSubmissionDTO temp : dto) {
			if (temp.getFieldId().contentEquals("recenzentiNaucnihOblasti")) {
				System.out.println("DEBUG: Naisli smo na listu recenzenata");
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if (listTemp.size() < 2) {
					System.out.println("DEBUG: Nisu izabrana bar 2 recenzenta");
					throw new RequestDataException("Morate izabrati makar dva recenzenta.");
				}
			}
		}

		HashMap<String, Object> map = this.mapListToDto2(dto);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		System.err.println("task name: " + task.getName());

		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("processInstanceId: " + processInstanceId.toString());

		runtimeService.setVariable(processInstanceId, "uredniciIrecenzenti", dto); // u uredniciIrecenzenti variablu smo
																					// stavili dto

		// submituj task form
		System.out.println(map);
		formService.submitTaskForm(taskId, map);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Izmena casopisa end point. Nakon submit-a forme se complete-uje zadatak što
	 * omogućava nastavak procesa.
	 * 
	 * @param dto
	 * @param taskId
	 * @param authentication
	 * @return
	 */
	@PreAuthorize("hasAnyRole('EDITORINCHIEF')")
	@PostMapping(path = "/editJournal/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> editJournal(@RequestBody List<FormSubmissionDTO> dto,
			@PathVariable String taskId, Authentication authentication) {
		System.err.println("editJournal endpoint: ");

		HashMap<String, Object> map = this.mapListToDto(dto);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
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
