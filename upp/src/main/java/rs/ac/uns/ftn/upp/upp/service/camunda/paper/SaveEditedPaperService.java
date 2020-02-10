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
import rs.ac.uns.ftn.upp.upp.model.journal.PaperText;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperTextService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.ReviewService;

@Service
public class SaveEditedPaperService implements JavaDelegate {

	@Autowired
	private PaperService paperService;
	
	@Autowired
	private PaperTextService paperTextService;

	// cuvamo recenzije rada (preporuku, komentar autoru, komentar uredniku i ko je recenzent)
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u SaveEditedPaperService");
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

		
		for(FormSubmissionDTO formField: form) {
			if (formField.getFieldId().equals("fileUpload")) {				
				PaperText paperText = paper.getPaperText();
				paperText.setText(((String)formField.getFieldValue()).getBytes());
				paperTextService.savePaperText(paperText);
				paper.setPaperText(paperText);
			}
		}
		
		paperService.savePaper(paper);
		
		System.err.println("izasao iz SaveEditedPaperService");

	}

}
