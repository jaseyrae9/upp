package rs.ac.uns.ftn.upp.upp.service.camunda;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class TestService implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u test");

		List<String> istekli = (List<String>) execution.getVariable("istekli");
		System.out.println("istekli.size(): " + istekli.size());
		for(String i: istekli) {
			System.out.println("istekao: " + i);
		}
		
		System.err.println("izasao iz test");

	}

}
