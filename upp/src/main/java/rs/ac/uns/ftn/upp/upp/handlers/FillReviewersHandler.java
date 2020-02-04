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
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class FillReviewersHandler implements TaskListener {

	@Autowired
	private FormService formService;

	@Autowired
	private PaperService paperService;

	// izvlacimo recenzente koji rade za casopis u kojem se rad nalazi
	// i koji se bave naucnom oblascu za koju je rad prijavljen
	public void notify(DelegateTask delegateTask) {
		System.err.println("Usli u fill reviewers handler");

		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());
		// delegateTask.getVariable("pokretac")
		List<FormField> formFields = tfd.getFormFields();
		Integer paperId = (Integer) delegateTask.getVariable("radId");
		Optional<Paper> optPaper = paperService.findById(paperId);
		if (!optPaper.isPresent()) {
			System.err.println("nema rada sa id: " + paperId);
			return;
		}
		Paper paper = optPaper.get();
		AcademicField paperAcademicField = paper.getAcademicField();
		Journal journal = paper.getJournal();
		Set<Customer> journalReviewers = journal.getJournalReviewers();
		Set<Customer> reviewers = new HashSet<>();
		Set<String> usersList = new HashSet<>();
		Customer editorInChief = journal.getEditorInChief();
		if (journalReviewers.isEmpty()) {
			System.err.println("ne postoje recenzenti u casopisu " + journal.getName());
			reviewers.add(editorInChief);
			usersList.add(editorInChief.getUsername());
			delegateTask.setVariable("usersList", usersList);
		} else {
			System.err.println("postoje recenzenti u casopisu " + journal.getName());
			for (Customer journalReviewer : journalReviewers) {
				if (journalReviewer.getCustomerAcademicFields().contains(paperAcademicField)) {
					System.err.println("ima recenzenata bas od te naucne oblasti");
					reviewers.add(journalReviewer);
				}
			}
			if (!reviewers.isEmpty()) {
				System.out.println("postoje recenzenti te naucne oblasti");
				for (Customer r : reviewers) {
					System.err.println("reviewers : " + r.getUsername());
					usersList.add(r.getUsername());
				}
				delegateTask.setVariable("usersList", usersList);
			} else {
				System.out.println("ne postoje recenzenti te naucne oblasti");
				reviewers.add(editorInChief);
				usersList.add(editorInChief.getUsername());
				delegateTask.setVariable("usersList", usersList);
			}
		}

		Map<String, String> itemsReviewers = new HashMap<>();

		if (!formFields.isEmpty()) {
			System.err.println("prvi if");
			for (FormField field : formFields) {
				System.err.println("field " + field.getId());
				if (field.getId().equals("recenzenti")) {
					EnumFormType eft = (EnumFormType) field.getType();
					itemsReviewers = eft.getValues();
					itemsReviewers.clear(); // Praznimo mapu jer nekako vuce stare vrednosti
					System.err.println("drugi if");
					for (Customer reviewer : reviewers) {
						System.out.println("dodato polje reviewer: " + reviewer.getId() + " vrednost: " + reviewer.getUsername());
						itemsReviewers.put(reviewer.getId().toString(), reviewer.getUsername());
					}
				}
			}
		}

		System.err.println("izasli iz fill reviewers handler");

	}
}