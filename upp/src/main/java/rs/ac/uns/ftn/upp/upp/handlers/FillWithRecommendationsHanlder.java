package rs.ac.uns.ftn.upp.upp.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.Recommendation;
import rs.ac.uns.ftn.upp.upp.model.journal.Review;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class FillWithRecommendationsHanlder implements TaskListener {
	
	@Autowired
	private PaperService paperService;
	
	@Autowired
	private FormService formService;

	public void notify(DelegateTask delegateTask) {
		System.err.println("Kreiran task za prikaz preporuka recenzenata uredniku");
		Integer paperId = Integer.parseInt(String.valueOf(delegateTask.getVariable("radId")));

		// Integer paperId = (Integer) delegateTask.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			System.out.println("Nema rada sa id: " + paperId);
		}
		
		Paper paper = paperOpt.get();
		Set<Review> reviews = paper.getReviews();
		System.out.println("reviews size: " + reviews.size());
		List<String> recommendations = new ArrayList<>();
		
		for(Review review: reviews) {
			if(review.getRecommendation() == Recommendation.ACCEPTED) {
				System.out.println("ACCEPTED" );
				recommendations.add("Prihvatiti");
			}
			if(review.getRecommendation() == Recommendation.MINOR_EDITS) {
				System.out.println("Manje ispravke" );
				recommendations.add("Manje ispravke");
			}
			if(review.getRecommendation() == Recommendation.MAJOR_EDITS) {
				System.out.println("Vece ispravke" );
				recommendations.add("Vece ispravke");
			}
			if(review.getRecommendation() == Recommendation.REJECT) {
				System.out.println("Odbiti" );
				recommendations.add("Odbiti");
			}
		}
		
		
		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());
		
		List<FormField> formFields = tfd.getFormFields();
		Map<String, String> itemRecommendations = new HashMap<>();
		
		if(!formFields.isEmpty()) {
			System.err.println("prvi if");
			for(FormField field : formFields) {
				System.err.println("field " + field.getId());
				if(field.getId().equals("preporukeRecenzenata")) {
					EnumFormType eft = (EnumFormType)field.getType();
					itemRecommendations = eft.getValues();
					itemRecommendations.clear(); // Praznimo mapu jer nekako vuce stare vrednosti
					System.err.println("drugi if");
					Integer counter = 0;
					for(String recommendation: recommendations) {
						counter += 1;
						 String preporuka = StringUtils.deleteWhitespace(recommendation);
						 //String original;
						 // original = original + StringUtils.repeat(" ", counter);
						itemRecommendations.put(preporuka.toLowerCase() + counter.toString(), recommendation + StringUtils.repeat(" ", counter));

						System.out.println("dodato polje preporuka: " + preporuka.toLowerCase() + counter.toString() + " vrednost: " + recommendation + StringUtils.repeat(" ", counter));
					}
				}
			}
		}	

	}
}