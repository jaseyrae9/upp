package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class ValidatePaper implements JavaDelegate {

	@Autowired
	private PaperService paperService;
	
	// ovo je servisni task za cuvanje izbora casopisa
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u validacija service");

		// uzimamo ono sto je bilo setovano kao variabla dto (kada se komplitovao user
		// task)
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");

		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}

		execution.setVariable("ispravniPodaci", "true");

		for (FormSubmissionDTO formField : form) {

			if (formField.getFieldId().equals("naslovRada")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}
				Paper existingPaper = paperService.findByName(formField.getFieldValue().toString());
				if (existingPaper != null) {
					execution.setVariable("ispravniPodaci", "false");
				}
				
			}
			if (formField.getFieldId().equals("imeKoautora")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("emailKoautora")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("gradKoautora")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("drzavaKoautora")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("kljucniPojmoviRada")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("apstraktRada")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("naucnaOblast")) {
				if(formField.getFieldValue() == null) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
			if (formField.getFieldId().equals("pdfTekst")) {
				if(formField.getFieldValue().equals("")) {
					execution.setVariable("ispravniPodaci", "false");
				}				
				
			}
		}
		System.err.println("ispravniPodaci==" + execution.getVariable("ispravniPodaci"));
	}

}
