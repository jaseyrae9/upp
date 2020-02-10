package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class ActivateAuthorsMembershipService implements JavaDelegate {

	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private CustomerService customerService;

	/**
	 * Servisni task gde aktiviramo autoru clanarinu.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis gde glavni urednik odlucuje da li je rad tematski prihvatljiv");

		String username = identityService.getCurrentAuthentication().getUserId();
		Customer customer = customerService.findByUsername(username);
		customer.setActiveMembership(true);
		customerService.saveCustomer(customer);

		
		System.err.println("izasao iz servisaproveravamo da li autor ima aktivnu clanarinu");

	}

}
