package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class SaveReplacementReviewersService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	@Autowired
	private CustomerService customerService;

	// cuvamo zamenskog recenzenta
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u SaveReplacementReviewersService");
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();

		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");
		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}
		Set<Customer> istekli = (HashSet<Customer>) execution.getVariable("istekli");
		Set<String> istekliString = new HashSet<String>();
		for(Customer istekao: istekli) {
			istekliString.add(istekao.getUsername());
		}
		// Set<String> usersList = new HashSet<>();
		Set<String> usersList = (HashSet<String>) execution.getVariable("usersList");
		usersList.removeAll(istekliString);

		for (FormSubmissionDTO formField : form) {
			if (formField.getFieldId().equals("ponovoIzaberi")) {
				String reviewerUsername = (String) formField.getFieldValue();
				Customer reviewer = customerService.findByUsername(reviewerUsername);
				paper.getPaperReviewers().add(reviewer);
				reviewer.getReviewerPapers().add(paper);
				customerService.saveCustomer(reviewer);
				paperService.savePaper(paper);
				execution.setVariable("izabran", reviewerUsername);
				execution.setVariable("oneUserId", reviewerUsername);
				usersList.add(reviewerUsername);
				execution.setVariable("usersList", usersList);

			}
		}

		System.err.println("izasao iz SavePaperReviewersService");

	}

}
