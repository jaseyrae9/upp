package rs.ac.uns.ftn.upp.upp.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.JournalService;

@Service
public class FillChoosenDataHandler implements TaskListener {

	@Autowired
	private FormService formService;
	
	@Autowired
	private JournalService journalService;

	
	public void notify(DelegateTask delegateTask) {
		System.err.println("Kreiran task za popunjavanje unetih podataka casopisa");
		
		Integer journalId = (Integer) delegateTask.getVariable("idCasopis");
		System.err.println("DEBUGaaaaa: journalId " + journalId);

		Optional<Journal> opt = journalService.findById(journalId);
		
		if(!opt.isPresent()) {
			// TODO: exception
			System.err.println("nema casopisa sa id: " + journalId);
		}
		Journal journal = opt.get();

		System.err.println("nasao casopis: ");

		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());
		System.err.println("tfd ");

		// lista form fildova i ispise u konzoli

		List<FormField> formFields = tfd.getFormFields();
		System.err.println("heeej: " + formFields.size());
		Map<String, String> items = new HashMap<>();
		Map<String, String> itemsEditors = new HashMap<>();
		Map<String, String> itemsReviewers = new HashMap<>();

		if(!formFields.isEmpty()) {
			for(FormField field : formFields) {
				System.err.println("field " + field.getId());
				if(field.getId().equals("izabraneNaucneOblasti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					items = eft.getValues();
					items.clear(); // Praznimo mapu jer nekako vuce stare vrednosti

					for(AcademicField academicField: journal.getJournalAcademicFields()) {
						System.out.println("dodato polje: " + academicField.getId() + " vrednost: " + academicField.getName());
						items.put(academicField.getId().toString(), academicField.getName());
						
					}
				}
				if(field.getId().equals("izabraniUrednici")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemsEditors = eft.getValues();
					itemsEditors.clear(); // Praznimo mapu jer nekako vuce stare vrednosti

					for(Customer customer: journal.getEditors()) {
						System.out.println("itemsEditors dodato polje: " + customer.getId() + " vrednost: " + customer.getUsername());
						itemsEditors.put(customer.getId().toString(), customer.getUsername());
					}
				}
				if(field.getId().equals("izabraniRecenzenti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemsReviewers = eft.getValues();
					itemsReviewers.clear(); // Praznimo mapu jer nekako vuce stare vrednosti
					for(Customer customer: journal.getJournalReviewers()) {
						System.out.println("itemsReviewers dodato polje: " + customer.getId() + " vrednost: " + customer.getUsername());
						itemsReviewers.put(customer.getId().toString(), customer.getUsername());
					}
				}
				
			}
		}	

	}
}