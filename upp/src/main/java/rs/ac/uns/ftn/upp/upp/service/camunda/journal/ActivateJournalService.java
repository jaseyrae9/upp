package rs.ac.uns.ftn.upp.upp.service.camunda.journal;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.JournalService;

@Service
public class ActivateJournalService implements JavaDelegate {

	@Autowired
	private JournalService journalService;

	// servisni task za aktiviranje casopisa, da sacuva u bazi da je aktivan
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u aktiviranje casopisa");

		Integer journalId = (Integer) execution.getVariable("idCasopis");
		Optional<Journal> opt = journalService.findById(journalId);

		if (!opt.isPresent()) {
			System.err.println("nema casopisa sa id: " + journalId);
			throw new NotFoundException(journalId, Journal.class.getSimpleName());
		}
		Journal journal = opt.get();
		
		journal.setActive(true);
		journalService.saveJournal(journal);
		System.err.println("casopis je aktiviran");

	}

}
