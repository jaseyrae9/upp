package rs.ac.uns.ftn.upp.upp.service.camunda.user;

import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.MyUser;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.service.emailservice.EmailService;
import rs.ac.uns.ftn.upp.upp.service.emailservice.VerificationTokenService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.UserService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class RegisterService implements JavaDelegate {

	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private AcademicFieldService academicFieldService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private VerificationTokenService tokenService;
	
	// ovo je servisni task za kreiranje korisnika, da ga sacuva u bazi
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u register servise");
		
		// uzimamo ono sto je bilo setovano kao variabla registration (kada se komplitovao user task) i od toga napravili usera
		List<FormSubmissionDTO> registration = (List<FormSubmissionDTO>) execution.getVariable("registration");
		
		for (FormSubmissionDTO fp : registration) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}
		
		Customer customer = new Customer();
		for(FormSubmissionDTO formField: registration) {
			if(formField.getFieldId().equals("ime")) {
				customer.setFirstName((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("prezime")) {
				customer.setLastName((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("grad")) {
				customer.setCity((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("drzava")) {
				customer.setCountry((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("titula")) {
				customer.setTitle((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("email")) {
				customer.setEmail((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("username")) {
				customer.setUsername((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("sifra")) {
				customer.setPassword(passwordEncoder.encode((String)formField.getFieldValue()));
			}
			if(formField.getFieldId().equals("recenzent")) {
				if(formField.getFieldValue().equals("") || formField.getFieldValue().equals("false") ) {
					customer.setWantsToBeReviewer(false);
				} else {
					customer.setWantsToBeReviewer(true);
				}
			}			
			if(formField.getFieldId().equals("naucneOblasti")) {
				List<String> naucneOblasti = (List<String>)formField.getFieldValue();
				
				for(String naucnaOblast : naucneOblasti) {
					AcademicField academicField = academicFieldService.findByName(naucnaOblast);
					customer.getCustomerAcademicFields().add(academicField);
				}
				
			}
		}
		
		registerCustomer(customer, "CUSTOMER");
		
		String executionId = execution.getId();
		System.err.println("execution id " + executionId );
		
		// pokusaj za mejl		
		confirmRegistration(customer, executionId);
		
	}
	
	/**
	 * Creates email confirmation message and send it to user.
	 * 
	 * @param customer to whom message is being sent
	 * @throws MessagingException
	 * @throws InterruptedException
	 * @throws MailException
	 */
	private void confirmRegistration(Customer customer, String executionId) throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
		String token = UUID.randomUUID().toString();
		System.err.println("token: " + token);

		// Cuvanje tokena za customera
		tokenService.createVerificationToken(customer, token);

		String recipientMail = customer.getEmail();
		System.err.println("mmejl: " + recipientMail);
		String subject = "Potvrda registracije";
		String confirmationUrl = "http://localhost:8080/register/confirmRegistration/" + executionId + "?token=" + token;
		String message = "<html><body>Kliknite ovde kako biste aktivirali nalog<br>" + confirmationUrl + "</body></html>";
		emailService.sendNotificaitionAsync(recipientMail, subject, message);
	}
	
	private void registerCustomer(MyUser user, String role) {
		System.out.println("registerCustomer metoda");
		Authority authority = authorityService.findByName(role);
		user.getUserAuthorities().add(authority);
		userService.saveUser(user);
	}
}
