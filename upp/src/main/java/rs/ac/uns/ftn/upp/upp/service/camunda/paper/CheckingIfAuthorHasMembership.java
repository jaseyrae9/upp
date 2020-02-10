package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class CheckingIfAuthorHasMembership implements JavaDelegate {

	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private CustomerService customerService;

	/**
	 * Servisni task gde proveravamo da li autor ima aktivnu clanarinu
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis gde proveravamo da li autor ima aktivnu clanarinu");

		String username = identityService.getCurrentAuthentication().getUserId();
		Customer customer = customerService.findByUsername(username);
		if(customer.getActiveMembership()) {
			execution.setVariable("aktivnaClanarina", true);
		} else {
			execution.setVariable("aktivnaClanarina", false);

		}

		
		System.err.println("izasao iz servisa gde proveravamo da li autor ima aktivnu clanarinu");

	}

}
