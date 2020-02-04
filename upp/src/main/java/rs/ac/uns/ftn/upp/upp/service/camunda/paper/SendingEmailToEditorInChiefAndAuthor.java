package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.emailservice.EmailService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class SendingEmailToEditorInChiefAndAuthor implements JavaDelegate {

	@Autowired
	private JournalService journalService;
	
	@Autowired
	private PaperService paperService;
		
	@Autowired
	private EmailService emailService;
	
	/**
	 * Izbor glavnog urednika casopisa u koji se rad dodaje.
	 * Slanje mejla o prijavi novog rada u sistem glavnom uredniku casopisa i autoru rad.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis za biranje glavnog urednika");

		Integer journalId = (Integer)execution.getVariable("izabraniCasopisId");
		Optional<Journal> journalOpt = journalService.findById(journalId);
		if(!journalOpt.isPresent()) {
			throw new NotFoundException(journalId, Journal.class.getSimpleName());
		}
		
		Journal journal = journalOpt.get();
		Customer editorInChief = journal.getEditorInChief();
		// nasla glavnog urednika i sacuvala ga u variabli kako bi mogao da se koristi za dodele taskova
		execution.setVariable("izabranGlavniUrednik", editorInChief.getUsername());
		
		Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		Customer author = paper.getAuthor();
		String executionId = execution.getId();

		// poslati mejl glavnom uredniku i autoru rada 
		// TODO: skini komentar

		// sendEmail(editorInChief, author, paper.getName(), executionId);
		System.err.println("izasao iz servisa za odabir glavnog urednika");
		
	}

	// TODO: promeniti da se stvarno njima salje mej, trenutno je zakucano da se meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.
	/**
	 * Kreira se email poruka i salje korisnicima.
	 * @param editorInChief - glavni editor kojem se salje mejl
	 * @param author - autoru kojem se salje mejl
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(Customer editorInChief, Customer author, String paperName, String executionId) throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
		String token = UUID.randomUUID().toString();
		System.err.println("token: " + token);

//		String editorInChiefMail = editorInChief.getEmail();
//		String authorMail = author.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Prijava novog rada u sistem";
		// String confirmationUrl = "http://localhost:8080/register/confirmRegistration/" + executionId + "?token=" + token;
		String message = "<html><body>Rad " + paperName + " autora " + author.getUsername() + " je uspešno prijavljen.<br></body></html>";
		// emailService.sendNotificaitionAsync(editorInChiefMail, subject, message);
		// emailService.sendNotificaitionAsync(author, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}
	
}
