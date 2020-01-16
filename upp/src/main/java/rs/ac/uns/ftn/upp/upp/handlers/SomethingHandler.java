package rs.ac.uns.ftn.upp.upp.handlers;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.repository.AcademicFieldRepository;

@Service
public class SomethingHandler implements TaskListener {
	
	@Autowired
	private AcademicFieldRepository academicFieldRepository;
	
	@Autowired
	private FormService formService;

	// izvlacimo usere koje postoje u bazi
	public void notify(DelegateTask delegateTask) {
		System.err.println("something handler");

		
	}
}