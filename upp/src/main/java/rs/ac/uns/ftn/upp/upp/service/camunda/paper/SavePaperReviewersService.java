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
public class SavePaperReviewersService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	@Autowired
	private CustomerService customerService;

	// cuvamo recenzente rada
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u SavePaperReviewersService");
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		// Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();

		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");
		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}		

		Set<String> usersList = new HashSet<>();
		for(FormSubmissionDTO formField: form) {
			if(formField.getFieldId().equals("recenzenti")) {
				List<String> reviewers = (List<String>)formField.getFieldValue();				
				for(String reviewer : reviewers) {
					Customer customer = customerService.findByUsername(reviewer);	
					usersList.add(reviewer);
					paper.getPaperReviewers().add(customer);
					customer.getReviewerPapers().add(paper);
					customerService.saveCustomer(customer);
					paperService.savePaper(paper);
				}
			}
			
		}		
		execution.setVariable("usersList", usersList);
		
		System.err.println("izasao iz SavePaperReviewersService");

	}

}
