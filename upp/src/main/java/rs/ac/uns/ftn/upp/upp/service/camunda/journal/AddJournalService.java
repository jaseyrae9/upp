package rs.ac.uns.ftn.upp.upp.service.camunda.journal;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.MembershipFeeMethod;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class AddJournalService implements JavaDelegate {

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
		System.err.println("usao u add journal service");
		
		// uzimamo ono sto je bilo setovano kao variabla casopis (kada se komplitovao user task) i od toga napravili casopis
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("casopis");

		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}
		
		Journal journal = new Journal();
		for(FormSubmissionDTO formField: form) {
			if(formField.getFieldId().equals("naziv")) {
				journal.setName((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("issnBroj")) {
				journal.setIssn((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("nacinNaplateClanarine")) {
				if(formField.getFieldValue().equals("Autorima")) {
					journal.setMembershipFeeMethod(MembershipFeeMethod.AUTHORS);
				} 
				if(formField.getFieldValue().equals("Citaocima")) {
					journal.setMembershipFeeMethod(MembershipFeeMethod.READERS);
				}
			}
			
			if(formField.getFieldId().equals("naucneOblasti")) {
				List<String> naucneOblasti = (List<String>)formField.getFieldValue();				
				for(String naucnaOblast : naucneOblasti) {
					AcademicField academicField = academicFieldService.findByName(naucnaOblast);
					journal.getJournalAcademicFields().add(academicField);
				}				
			}		
		}
		
		String starter = (String) execution.getVariable("pokretac");
		System.err.println("starter->" + starter);
		Optional<Customer> editorInChief = customerService.findCustomer(starter);
		if(!editorInChief.isPresent()) {
			System.err.println("[addJournalService] ne postoji korisnik sa nazivom " + starter);
			throw new NotFoundException(starter, Customer.class.getSimpleName());
		}
		
		if(editorInChief.get().getJournal() != null) {
			System.out.println("Ovaj urednik vec ima casopis");
			throw new RequestDataException("Ovaj urednik već ima časopis.");

		} else {
			journal.setEditorInChief(editorInChief.get());
			Journal savedJournal = journalService.saveJournal(journal);
			
			editorInChief.get().setJournal(savedJournal);
			editorInChief.get().setAcceptedAsReviewer(true);
			
			Authority editorInChiefAuthority = authorityService.findByName("EDITORINCHIEF");
			editorInChief.get().getUserAuthorities().add(editorInChiefAuthority);
			
			customerService.saveCustomer(editorInChief.get());
			
			System.err.println("izasao iz add journal service");
		}
	}
	
	
}
