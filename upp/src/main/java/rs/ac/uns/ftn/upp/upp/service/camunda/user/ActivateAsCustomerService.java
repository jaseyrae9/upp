package rs.ac.uns.ftn.upp.upp.service.camunda.user;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class ActivateAsCustomerService implements JavaDelegate {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private IdentityService identityService;
	
	// servisni task za aktivaciju korisnika
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis da aktivira korisnika kao obicnog korisnika.");

		List<FormSubmissionDTO> registration = (List<FormSubmissionDTO>) execution.getVariable("registration");
		String password = "";
		for (FormSubmissionDTO formField : registration) {
			if(formField.getFieldId().equals("sifra")) {
				password = (String) formField.getFieldValue();
			}
			if (formField.getFieldId().equals("username")) {
				String username = (String) formField.getFieldValue();
				Optional<Customer> customerOpt = customerService.findCustomer(username);
				if (!customerOpt.isPresent()) {
					System.err.println("ne postoji korisnik sa korisnickim imenom: " + username);
					throw new NotFoundException(username, Customer.class.getSimpleName());
				}
				Customer customer = customerOpt.get();
				customer.setActive(true);
				Customer savedCustomer = customerService.saveCustomer(customer);
				
				User camundaUser = identityService.newUser(username);
				camundaUser.setFirstName(savedCustomer.getFirstName());
				camundaUser.setLastName(savedCustomer.getLastName());
				camundaUser.setPassword(password);
				camundaUser.setEmail(savedCustomer.getEmail());
				
				identityService.saveUser(camundaUser);
				identityService.createMembership(username, "customers");
			}
		}
		System.err.println("izasao iz servisa da aktivira korisnika kao obicnog korisnika");

	}
}
