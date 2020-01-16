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
public class ActivateJournalService implements JavaDelegate {

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
		System.err.println("usao u aktiviranje casopisa");

		Integer journalId = (Integer) execution.getVariable("idCasopis");
		Optional<Journal> opt = journalService.findById(journalId);

		if (!opt.isPresent()) {
			// TODO: exception
			System.err.println("nemaa tog casopisa");
		}
		Journal journal = opt.get();
		
		journal.setActive(true);
		journalService.saveJournal(journal);
		System.err.println("caopis je aktiviran");

	}

}
