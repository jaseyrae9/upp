package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.Recommendation;
import rs.ac.uns.ftn.upp.upp.model.journal.Review;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.ReviewService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class SavePaperReviewService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ReviewService reviewService;
	
	// cuvamo recenzije rada (preporuku, komentar autoru, komentar uredniku i ko je recenzent)
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u SavePaperReviewService");
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

		Review review = new Review();
		review.setPaper(paper);

		for(FormSubmissionDTO formField: form) {
			if(formField.getFieldId().equals("preporuka")) {
				if(formField.getFieldValue().equals("Prihvatiti")) {
					review.setRecommendation(Recommendation.ACCEPTED);
				}
				if(formField.getFieldValue().equals("Manje ispravke")) {
					review.setRecommendation(Recommendation.MINOR_EDITS);
				}
				if(formField.getFieldValue().equals("Vece ispravke")) {
					review.setRecommendation(Recommendation.MAJOR_EDITS);
				}
				if(formField.getFieldValue().equals("Odbiti")) {
					review.setRecommendation(Recommendation.REJECT);
				}
			}
			if(formField.getFieldId().equals("komentarAutoru")) {
				review.setCommentForAuthor((String)formField.getFieldValue());
			}
			if(formField.getFieldId().equals("komentarUrednicima")) {
				review.setCommentForEditor((String)formField.getFieldValue());
			}
		}
		
		String korisnik = (String) execution.getVariable("korisnik");
		Customer reviewer = customerService.findByUsername(korisnik);
		review.setReviewer(reviewer);
		Review savedReview = reviewService.saveReviewr(review);
		reviewer.getReviews().add(savedReview);
		customerService.saveCustomer(reviewer);
		
		// TODO: ovo je dodato ako vidis da nesto brlja, ovo je visak
		paper.getReviews().add(savedReview);
		paperService.savePaper(paper);
		//
		
		execution.setVariable("idReview", savedReview.getId());

		System.err.println("izasao iz SavePaperReviewService");

	}

}
