package rs.ac.uns.ftn.upp.upp.service.camunda.user;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class ActivateAsReviewerService implements JavaDelegate {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthorityService authorityService;
	
	// servisni task koji dodaje korisniku ulogu recenzenta
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis da aktivira korisnika kao recenzenta.");
	
		List<FormSubmissionDTO> registration = (List<FormSubmissionDTO>) execution.getVariable("registration");

		for (FormSubmissionDTO formField : registration) {
			if (formField.getFieldId().equals("username")) {
				String username = (String) formField.getFieldValue();
				Optional<Customer> customer = customerService.findCustomer(username);
				if(!customer.isPresent()) {
					System.err.println("ne postoji korisnik sa korisnickim imenom: " + username);
					throw new NotFoundException(username, Customer.class.getSimpleName());
				}
				customer.get().setAcceptedAsReviewer(true);
				Authority authority = authorityService.findByName("REVIEWER");
				customer.get().getUserAuthorities().add(authority);
				customerService.saveCustomer(customer.get());
			}
		}
		System.err.println("izasao iz servisa da aktivira korisnika kao recenzenta");
	}
}
