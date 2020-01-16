package rs.ac.uns.ftn.upp.upp.service.camunda.journal;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.MembershipFeeMethod;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class AddEditorsAndReviewersService implements JavaDelegate {

	@Autowired
	private JournalService journalService;
	
	@Autowired
	private AcademicFieldService academicFieldService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthorityService authorityService;
	
	// ovo je servisni task za kreiranje casopisa, da ga sacuva u bazi
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u AddEditorsAndReviewersService service");
		
		String journalName = (String) execution.getVariable("naziv");
		System.out.println("naziv casopisa " + journalName);
		
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("uredniciIrecenzenti");
		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}
		
		
		Optional<Journal> opt = journalService.findJournalByName(journalName);
		
		if(!opt.isPresent()) {
			// TODO: exception
			System.err.println("nemaa to casopisa");
		}
		
		Journal journal = opt.get();
		Integer journalId = journal.getId();
		execution.setVariable("idCasopis", journalId);
		System.err.println("DEBUG: CASOPIS ID " + journalId);
		
		for(FormSubmissionDTO formField: form) {
			if(formField.getFieldId().equals("uredniciNaucnihOblasti")) {
				List<String> editors = (List<String>)formField.getFieldValue();				
				for(String editor : editors) {
					Customer customer = customerService.findByUsername(editor);				
					journal.getEditors().add(customer);
					customer.setJournal(journal);
					customerService.saveCustomer(customer);
				}
			}
			if(formField.getFieldId().equals("recenzentiNaucnihOblasti")) {
				List<String> reviewers = (List<String>)formField.getFieldValue();				
				for(String reviewer : reviewers) {
					Customer customer = customerService.findByUsername(reviewer);
					journal.getJournalReviewers().add(customer);
					customer.getCustomerJournals().add(journal);
					customerService.saveCustomer(customer);
				}
			}
		}
		
		journalService.saveJournal(journal);
		System.err.println("izasao iz AddEditorsAndReviewersService sservica");
		
		
		
		
	}
	
	
}
