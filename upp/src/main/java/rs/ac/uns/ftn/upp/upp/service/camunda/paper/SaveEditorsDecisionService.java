package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Decision;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class SaveEditorsDecisionService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	// cuvamo odluku editora o radu - da li je prihvacen, da li treba manja ispravka, veca ispravka ili se odbija
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis gde se cuva odluka urednika");
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		// Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();

		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");

		for (FormSubmissionDTO formField : form) {
			if (formField.getFieldId().equals("odlukaUrednika")) {
				if (formField.getFieldValue().equals("Prihvatiti")) {
					System.out.println("Prihvatiti");
					paper.setDecision(Decision.ACCEPTED);
					paper.setAccepted(true);
				}
				if (formField.getFieldValue().equals("Manje ispravke")) {
					System.out.println("MINOR_EDITS");

					paper.setDecision(Decision.MINOR_EDITS);
				}
				if (formField.getFieldValue().equals("Vece ispravke")) {
					System.out.println("MAJOR_EDITS");

					paper.setDecision(Decision.MAJOR_EDITS);
				}
				if (formField.getFieldValue().equals("Odbiti")) {
					System.out.println("REJECT");

					paper.setDecision(Decision.REJECT);
					paper.setAccepted(false);
				}
			}
		}

		paperService.savePaper(paper);
		System.err.println("izasao iz servisa gde se cuva odluka urednika");

	}

}
