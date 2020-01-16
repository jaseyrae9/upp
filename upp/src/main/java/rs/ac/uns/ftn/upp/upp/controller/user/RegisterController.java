package rs.ac.uns.ftn.upp.upp.controller.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.ac.uns.ftn.upp.upp.dto.FormFieldsDTO;
import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.AuthenticationResponse;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.VerificationToken;
import rs.ac.uns.ftn.upp.upp.security.auth.JwtAuthenticationRequest;
import rs.ac.uns.ftn.upp.upp.security.auth.TokenUtils;
import rs.ac.uns.ftn.upp.upp.service.emailservice.VerificationTokenService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.CustomUserDetailsService;

@Controller
@RequestMapping("/register")
public class RegisterController {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private VerificationTokenService tokenService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private AcademicFieldService academicFieldService;

	/**
	 * Pokrece se proces (kad se klikne dugme u navbaru registracija)
	 * i uzima prvi user task iz bpmn modela i polja forme tog user taska, zatim ta polja saljemo frontu.	 *  
	 * 
	 * @return formfield dto za front
	 */
	
	@GetMapping(path = "/get", produces = "application/json")
	public @ResponseBody FormFieldsDTO get() {
		System.err.println("U kontroleru za slanje forme");
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("registracijaKorisnika");
		System.err.println("pii: " + pi.getProcessInstanceId());
		// uzimamo prvi task
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);
		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(task.getId());

		// lista form fildova i ispise u konzoli
		List<FormField> properties = tfd.getFormFields();
		for (FormField fp : properties) {
			System.out.println(fp.getId() + fp.getType());
		}

		// pravi dto u koji smesti taj task id, process instance id i te form fieldove i to se salje na front
		return new FormFieldsDTO(task.getId(), pi.getId(), properties);
	}

	
	/**
	 * Registracija korisnika endpoint, complituje se user task za unos podataka, klikom na dugme iz forme za registraiju,
	 * da bi nastavili dalje.
	 * 
	 * @param dto podaci za registraciju
	 * @param taskId id taska koji se submituje
	 * @return
	 */
	@PostMapping(path = "/post/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> post(@Valid @RequestBody List<FormSubmissionDTO> dto, @PathVariable String taskId) {
		System.err.println("registracija korisnika endpoint: ");

		HashMap<String, Object> map = this.mapListToDto(dto);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null ili task sa tim id-om
		System.err.println("task name: " + task.getName());
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("processInstanceId: " + processInstanceId.toString());

		// ocemo da postavimo procesnu variablu bas na taj processInstanceId iz kojeg je taj task protekao
		// registration se zove variabla
		// stavi taj dto koji je korisnik poslao
		runtimeService.setVariable(processInstanceId, "registration", dto); // u registration variablu smo stavili dto

//		try {
			formService.submitTaskForm(taskId, map);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}	
	
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Confirms email of registered user.
	 * 
	 * @param token
	 * @return
	 */
	// kad se klikne link iz mejla
	@RequestMapping(value = "/confirmRegistration/{processInstanceId}", method = RequestMethod.GET)
	public ResponseEntity<?> confirmsEmail(@RequestParam("token") String token, @PathVariable String processInstanceId,
			HttpServletResponse httpServletResponse) {
		Optional<VerificationToken> verificationToken = tokenService.findByToken(token);

		System.out.println("pronadjen token-> " + verificationToken.get().getToken());
		if (!verificationToken.isPresent()) {
			System.err.println("TODO");
			// return
			// ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.LOCATION,
			// "https://ticket-reservation21.herokuapp.com/error/404").build();
		}

		Customer customer = (Customer) verificationToken.get().getUser();
		customer.setConfirmedMail(true);
		customerService.saveCustomer(customer);

		// omogucavav prelazak dalje
		MessageCorrelationResult results = runtimeService.createMessageCorrelation("PotvrdaMejla")
				.processInstanceId(processInstanceId).correlateWithResult();
		System.err.println("results: " + results);

		System.err.println("izasao potvrde mejla");

		// redirekcija gde zelimo
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:4200/login")
				.build();
	}

	private HashMap<String, Object> mapListToDto(List<FormSubmissionDTO> list) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (FormSubmissionDTO temp : list) {
			System.err.println("sve " + temp.getFieldId());

			if(temp.getFieldValue() instanceof List) {
				List<String> listTemp = (List<String>)temp.getFieldValue();
				if(!listTemp.isEmpty()) {
					System.err.println("LSITA " + temp.getFieldId());
					AcademicField af = academicFieldService.findByName(listTemp.get(0));
					map.put(temp.getFieldId(), af.getId().toString());	
				}
			} else {
				map.put(temp.getFieldId(), temp.getFieldValue());	
			}
		}

		return map;
	}

	/**
	 * Tries to login user.
	 * 
	 * @param authenticationRequest contains email and password
	 * @param response
	 * @param device                type of device on which user wants to login
	 * @return
	 * @throws AuthenticationException
	 * @throws IOException
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(
			@Valid @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response,
			Device device) throws AuthenticationException, IOException {

		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
						authenticationRequest.getPassword()));

		// Ubaci username + password u kontext
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Kreiraj token
		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		String token = this.tokenUtils.generateToken(userDetails, device);

		// Vrati token kao odgovor na uspesno autentifikaciju
		return ResponseEntity.ok(new AuthenticationResponse(token));
	}


	// admin odlucuje endpoint - completujemo user task admin potvrđuje/odbija recenzenta
	// -> kad se klikne dugme iz forme za odlucivanje
	// da bi nastavili dalje
	// salju nam se podaci sa fronta
	@PostMapping(path = "/decide/{taskId}", produces = "application/json")
	public @ResponseBody ResponseEntity<?> decide(@RequestBody List<FormSubmissionDTO> dto, @PathVariable String taskId) {
		System.err.println("decide endpoint: ");

		HashMap<String, Object> map = this.mapListToDto(dto);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); // single result jer ce dati ili null ili task sa tim id-om
		System.err.println(" decide task name: " + task.getName());
		String processInstanceId = task.getProcessInstanceId(); // iz taska izvlacimo proces instance id
		System.err.println("decide->processInstanceId: " + processInstanceId.toString());

		runtimeService.setVariable(processInstanceId, "decision", dto); // u decision variablu smo stavili dto

		// submituj task form, ako smo stavljali validacije u bpmn modelu, puce ako nije
		// ispostovana validacija
		// mozemo tu parcijalnu validaciju hendlovati ovde pre nego sto complitujemo
		// task
		// try catch ako vrisne vrati nazad
		try {
			formService.submitTaskForm(taskId, map);
		} catch (Exception e) {
			// TODO:
			System.err.println("bbb");
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
