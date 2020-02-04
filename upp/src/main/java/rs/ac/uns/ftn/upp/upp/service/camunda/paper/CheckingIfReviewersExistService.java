package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class CheckingIfReviewersExistService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	/**
	 * Provera da li postoje recenzenti naucne oblasti rada.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u service koji proverava da li postoje recenzenti naucne oblasti za koju je rad prijavljen.");

		Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		AcademicField paperAcademicField = paper.getAcademicField();
		Journal journal = paper.getJournal();
		
		// proveriti da li postoje recenzenti i sacuvati u variablu nemaRecenzenata
		// nemaRecenzenata == false -> ima ih
		// nemaRecenzenata == true -> nema ih
		Set<Customer> journalReviewers = journal.getJournalReviewers();
		execution.setVariable("nemaRecenzenata", true);
		if(journalReviewers.isEmpty()) {
			execution.setVariable("nemaRecenzenata", true);
		} else {
			for (Customer journalReviewer: journalReviewers) {
				if (journalReviewer.getCustomerAcademicFields().contains(paperAcademicField)) {
					System.err.println("ima recenzenata bas od te naucne oblasti");
					execution.setVariable("nemaRecenzenata", false);
				} 
			}
		}
		System.out.println("nemaRecenzenta=" + execution.getVariable("nemaRecenzenata"));
		System.err.println("izasao iz servica koji proverava da li postoje recenzenti naucne oblasti za koju je rad prijavljen.");
		
	}

	

}
