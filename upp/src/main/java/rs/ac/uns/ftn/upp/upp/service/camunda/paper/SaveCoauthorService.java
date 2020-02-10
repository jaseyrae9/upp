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
import rs.ac.uns.ftn.upp.upp.model.user.Coauthor;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CoauthorService;

@Service
public class SaveCoauthorService implements JavaDelegate {

	@Autowired
	private CoauthorService coauthorService;
	
	@Autowired
	private PaperService paperService;

	// cuvamo recenzije rada (preporuku, komentar autoru, komentar uredniku i ko je recenzent)
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u SaveCoauthorService");
	
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");
			
		Coauthor coauthor = new Coauthor();
		for(FormSubmissionDTO formField: form) {
			if (formField.getFieldId().equals("imeKoautora")) {				
				coauthor.setName((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("emailKoautora")) {				
				coauthor.setEmail((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("gradKoautora")) {				
				coauthor.setCity((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("drzavaKoautora")) {				
				coauthor.setCountry((String) formField.getFieldValue());
			}
		}
		
		
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		coauthor.setPaper(paper);
		Coauthor savedCoauthor = coauthorService.saveCoauthor(coauthor);

		paper.getCoauthors().add(savedCoauthor);
		paperService.savePaper(paper);		
		
		System.err.println("izasao iz SaveCoauthorService");

	}

}
