package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class AddPaperService implements JavaDelegate {

	@Autowired
	private JournalService journalService;
	
	@Autowired
	private PaperService paperService;
	
	@Autowired
	private AcademicFieldService academicFieldService;

	@Autowired
	private CustomerService customerService;

	// ovo je servisni task za kreiranje rada, da ga sacuva u bazi
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u add paper service");

		// uzimamo ono sto je bilo setovano kao variabla submit (kada se komplitovao
		// user task)
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) execution.getVariable("submit");

		for (FormSubmissionDTO fp : form) {
			System.out.println(fp.getFieldId() + " " + fp.getFieldValue());
		}

		Integer paperId = (Integer) execution.getVariable("radId");
		Paper paper = new Paper();

		if(paperId != null) {
			Optional<Paper> paperOpt = paperService.findById(paperId);
			if(!paperOpt.isPresent()) {
				throw new NotFoundException(paperId, Paper.class.getSimpleName());
			}
			paper = paperOpt.get();
		} 	

		for (FormSubmissionDTO formField : form) {
			if (formField.getFieldId().equals("naslovRada")) {
				paper.setName((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("imeKoautora")) {
				paper.setCoAutorName((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("emailKoautora")) {
				paper.setCoAutorEmail((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("gradKoautora")) {
				paper.setCoAutorCity((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("drzavaKoautora")) {
				paper.setCoAutorCountry((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("kljucniPojmoviRada")) {
				paper.setKeyTerms((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("apstraktRada")) {
				paper.setAbstractOfPaper((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("pdfTekst")) {
				paper.setText((String) formField.getFieldValue());
			}

			if (formField.getFieldId().equals("naucnaOblast")) {
				AcademicField academicField = academicFieldService.findByName((String) formField.getFieldValue());
				paper.setAcademicField(academicField);
			}
		}
		String starter = (String) execution.getVariable("pokretacAutor");
		System.err.println("starter->" + starter);
		Optional<Customer> authorOpt = customerService.findCustomer(starter);
		if(!authorOpt.isPresent()) {
			System.err.println("[addPaperService] ne postoji korisnik sa nazivom " + starter);
			throw new NotFoundException(starter, Customer.class.getSimpleName());
		}
		Customer author = authorOpt.get();
		paper.setAuthor(author);
		
		Paper paperSaved = paperService.savePaper(paper);
		Integer journalId = (Integer)execution.getVariable("izabraniCasopisId");
		Optional<Journal> journalOpt = journalService.findById(journalId);
		if(!journalOpt.isPresent()) {
			throw new NotFoundException(journalId, Journal.class.getSimpleName());
		}
		
		Journal journal = journalOpt.get();
		paperSaved.setJournal(journal);
		journal.getPapers().add(paperSaved);
		journalService.saveJournal(journal);
		
		execution.setVariable("radId", paperSaved.getId());	
		
		System.err.println("izasao iz add paper service");
		
		
		
	}

}
