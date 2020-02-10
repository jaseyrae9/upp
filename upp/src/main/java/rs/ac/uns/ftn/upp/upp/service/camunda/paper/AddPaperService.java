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
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.PaperText;
import rs.ac.uns.ftn.upp.upp.model.user.Coauthor;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.EditionService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperTextService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CoauthorService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class AddPaperService implements JavaDelegate {

	@Autowired
	private JournalService journalService;

	@Autowired
	private PaperService paperService;

	@Autowired
	private EditionService editionService;

	@Autowired
	private AcademicFieldService academicFieldService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PaperTextService paperTextService;

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

		// Integer paperId = (Integer) execution.getVariable("radId");
		Integer paperId = null;
		try {
			paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Paper paper = new Paper();

		if (paperId != null) { // ako se menja
			Optional<Paper> paperOpt = paperService.findById(paperId);
			if (!paperOpt.isPresent()) {
				throw new NotFoundException(paperId, Paper.class.getSimpleName());
			}
			paper = paperOpt.get();
		}

		for (FormSubmissionDTO formField : form) {
			if (formField.getFieldId().equals("naslovRada")) {
				paper.setName((String) formField.getFieldValue());
			}			
			if (formField.getFieldId().equals("kljucniPojmoviRada")) {
				paper.setKeyTerms((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("apstraktRada")) {
				paper.setAbstractOfPaper((String) formField.getFieldValue());
			}
			if (formField.getFieldId().equals("naucnaOblast")) {

				AcademicField academicField = academicFieldService.findByName((String) formField.getFieldValue());
				paper.setAcademicField(academicField);
			}
			if (formField.getFieldId().equals("fileUpload")) {
				if (paperId == null) {
					PaperText paperText = new PaperText();
					paperText.setText(((String) formField.getFieldValue()).getBytes());
					PaperText saved = paperTextService.savePaperText(paperText);
					paper.setPaperText(saved);
				} else {
					PaperText paperText = paper.getPaperText();
					paperText.setText(((String) formField.getFieldValue()).getBytes());
					paperTextService.savePaperText(paperText);
					paper.setPaperText(paperText);
				}

			}
		}
		String starter = (String) execution.getVariable("pokretacAutor");
		System.err.println("starter->" + starter);
		Optional<Customer> authorOpt = customerService.findCustomer(starter);
		if (!authorOpt.isPresent()) {
			System.err.println("[addPaperService] ne postoji korisnik sa nazivom " + starter);
			throw new NotFoundException(starter, Customer.class.getSimpleName());
		}
		Customer author = authorOpt.get();
		paper.setAuthor(author);

		Paper paperSaved = paperService.savePaper(paper);

		Integer journalId = (Integer) execution.getVariable("izabraniCasopisId");
		Optional<Journal> journalOpt = journalService.findById(journalId);
		if (!journalOpt.isPresent()) {
			throw new NotFoundException(journalId, Journal.class.getSimpleName());
		}

		Journal journal = journalOpt.get(); // nasli smo izabrani casopis
		Edition edition = new Edition(); // rad se dodaje u izdanje, a casopis ima izdanja

		for (Edition e : journal.getJournalEditions()) { // prolazimo kroz izdanja casopisa i trazimo ne objavljeno
															// izdanje
			if (!e.getPublished()) { // ako nije objavljeno izdanje u taj dodajemo rad
				edition = new Edition(e);
				break;
			}
		}

		// paperSaved.setJournal(journal);
		paperSaved.setEdition(edition);

		// journal.getPapers().add(paperSaved);
		// journalService.saveJournal(journal);
		edition.getPapers().add(paperSaved);
		editionService.saveEdition(edition);

		execution.setVariable("radId", paperSaved.getId());

		System.err.println("izasao iz add paper service");

	}

}
