package rs.ac.uns.ftn.upp.upp.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.repository.JournalRepository;

@Service
public class FillAllJournalsHandler implements TaskListener {
	
	@Autowired
	private JournalRepository journalRepository;
	
	@Autowired
	private FormService formService;

	// izvlacimo usere koje postoje u bazi
	public void notify(DelegateTask delegateTask) {
		System.err.println("Kreiran prvi task-popunjavamo casopise");

		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());
		
		List<FormField> formFields = tfd.getFormFields();
		Map<String, String> itemsJournals = new HashMap<>();
		Iterable<Journal> journals = journalRepository.findAllByActive(true);
		
		if(!formFields.isEmpty()) {
			System.err.println("prvi if");
			for(FormField field : formFields) {
				System.err.println("field " + field.getId());
				if(field.getId().equals("casopisi")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemsJournals = eft.getValues();
					itemsJournals.clear(); // Praznimo mapu jer nekako vuce stare vrednosti
					System.err.println("drugi if");
					for(Journal journal: journals) {
						System.out.println("dodato polje journal: " + journal.getId() + " vrednost: " + journal.getName());
						itemsJournals.put(journal.getId().toString(), journal.getName());
					}
				}
			}
		}	

	}
}