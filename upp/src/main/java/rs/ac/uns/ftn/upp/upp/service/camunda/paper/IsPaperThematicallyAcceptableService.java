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
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class IsPaperThematicallyAcceptableService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	/**
	 * Servisni task gde cuvamo odluku glavnog urednika o tome da li je rad tematski prihvatljiv.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis gde glavni urednik odlucuje da li je rad tematski prihvatljiv");

		// uzimamo ono sto je bilo setovano kao variabla submit (kada se komplitovao
		// user task)
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");

		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}

		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		for (FormSubmissionDTO formField : form) {
			if (formField.getFieldId().equals("prihvatljiv")) {
				if(formField.getFieldValue().equals("") || formField.getFieldValue().equals("false") ) {
					paper.setIsThematicallyAcceptable(false);
				} else {
					paper.setIsThematicallyAcceptable(true);
				}
			}
		}		
		
		paperService.savePaper(paper);
		System.out.println("komenatUrednika: " + execution.getVariable("komenatUrednika"));
		System.err.println("izasao iz servisa gde glavni urednik odlucuje da li je rad tematski prihvatljiv");

	}

}
