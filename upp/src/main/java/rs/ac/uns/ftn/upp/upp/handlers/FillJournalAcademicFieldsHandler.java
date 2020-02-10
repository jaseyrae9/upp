package rs.ac.uns.ftn.upp.upp.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;

@Service
public class FillJournalAcademicFieldsHandler implements TaskListener {
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private JournalService journalService;


	@Override
	public void notify(DelegateTask delegateTask) {
	System.out.println(" Kreiran task za popunjavanje naucnih oblasti odabranog casopisa");
		
		// pronalazim naucne oblasti casopisa
		Set<AcademicField> afs = new HashSet<AcademicField>();		
		Integer journalId = (Integer) delegateTask.getVariable("izabraniCasopisId");
		Optional<Journal> journalOpt = journalService.findById(journalId);
		if(!journalOpt.isPresent()) {
			System.err.println("nema casopisa sa id: " + journalId);
		}
		Journal journal = journalOpt.get();
		afs.addAll(journal.getJournalAcademicFields());
		System.err.println("afs.size() " + afs.size());

		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());

		List<FormField> formFields = tfd.getFormFields();
		System.err.println("formFields.size: " + formFields.size());
		Map<String, String> itemsAF = new HashMap<>();
		if(!formFields.isEmpty()) {
			System.err.println("prvi if");
			for(FormField field : formFields) {
				if(field.getId().equals("naucnaOblast")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemsAF = eft.getValues();
					// itemsAF.clear(); // Praznimo mapu jer nekako vuce stare vrednosti
					System.err.println("drugi if");
					for(AcademicField academicField: afs) {
						System.out.println("dodato polje: " + academicField.getId() + " vrednost: " + academicField.getName());
						itemsAF.put(academicField.getId().toString(), academicField.getName());
					}
				}

			}
		}			
	}
}