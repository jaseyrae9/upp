package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.MembershipFeeMethod;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;

@Service
public class SaveChoosenJournal implements JavaDelegate {

	@Autowired
	private JournalService journalService;
	
	// ovo je servisni task za cuvanje izbora casopisa
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u add journal service");

		// uzimamo ono sto je bilo setovano kao variabla dto (kada se komplitovao user
		// task)
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("dto");

		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}

		// Journal journal = new Journal();
		// execution.setVariable("openAccess", journalId);

		for (FormSubmissionDTO formField : form) {

			if (formField.getFieldId().equals("casopisi")) {
				System.err.println("izabran: "  + formField.getFieldValue());
				Journal journal = journalService.findByName(formField.getFieldValue().toString());
				
				execution.setVariable("izabraniCasopisId", journal.getId());

				if(journal.getMembershipFeeMethod() == MembershipFeeMethod.AUTHORS) {
					// autori placaju, casopis je open-access
					execution.setVariable("openAccess", "true");
				}
				else {
					execution.setVariable("openAccess", "false");
				}

			}

		}
	}

}
