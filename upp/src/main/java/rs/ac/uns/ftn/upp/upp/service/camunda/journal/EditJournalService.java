package rs.ac.uns.ftn.upp.upp.service.camunda.journal;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.MembershipFeeMethod;
import rs.ac.uns.ftn.upp.upp.service.entityservice.JournalService;

@Service
public class EditJournalService implements JavaDelegate {

	@Autowired
	private JournalService journalService;

	// servisni task za izmenu casopisa
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u EditJournalService service");

		Integer journalId = (Integer) execution.getVariable("idCasopis");
		Optional<Journal> opt = journalService.findById(journalId);

		if (!opt.isPresent()) {
			System.err.println("nema casopisa sa id: " + journalId);
			throw new NotFoundException(journalId, Journal.class.getSimpleName());
		}
		Journal journal = opt.get();
		
		//izmenjenCasopis variabla
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("izmenjenCasopis");
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
			
		}
		
		journalService.saveJournal(journal);
		System.err.println("izasao iz edit journal service");

	}

}
