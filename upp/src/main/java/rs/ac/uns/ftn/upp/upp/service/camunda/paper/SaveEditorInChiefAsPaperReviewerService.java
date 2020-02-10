package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class SaveEditorInChiefAsPaperReviewerService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthorityService authorityService;

	// cuvamo glavnog urednika kao recenzenta rada
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u SaveEditorInChiefAsReviewerService");
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		// Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		Edition edition = paper.getEdition(); // rad pripada izadnju
		Journal journal = edition.getJournal(); // preko izdanja saznamo kom casopisu rad pripada
		Customer editorInChief = journal.getEditorInChief();

		String izabranGlavniUrednik = (String) execution.getVariable("izabranGlavniUrednik");
		if (izabranGlavniUrednik.contentEquals(editorInChief.getUsername())) {
			editorInChief.getReviewerPapers().add(paper);
			
			Authority reviewerAuthority = authorityService.findByName("REVIEWER");
			editorInChief.getUserAuthorities().add(reviewerAuthority);
			
			customerService.saveCustomer(editorInChief);
			paper.getPaperReviewers().add(editorInChief);
			paperService.savePaper(paper);
		} else {
			throw new RequestDataException(
					"Dodeljeni izabrani urednik: " + izabranGlavniUrednik + " ne odgovara glavnom uredniku: "
							+ editorInChief.getUsername() + " casopisa: " + journal.getName());
		}

		System.err.println("izasao iz SaveEditorInChiefAsReviewerService");

	}

}
