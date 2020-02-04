package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.Optional;

import javax.mail.MessagingException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.emailservice.EmailService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class NotifyAuthorAboutEditingPaperService implements JavaDelegate {

	@Autowired
	private PaperService paperService;
		
	@Autowired
	private EmailService emailService;
	
	/**
	 * Obavestavamo autora rada da je potrebno da ga izmeni u zadatom roku.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis za notifikaciju autora o zadatom roku za izmenu rada.");
			
		Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		Customer author = paper.getAuthor();
		String executionId = execution.getId();
		String deadline = (String) execution.getVariable("rok");

		// poslati mejl autoru rada 
		// TODO: skini komentar
		// sendEmail(author, paper.getName(), deadline, executionId);
		System.err.println("izasao iz servisa za notifikaciju autora o zadatom roku za izmenu rada");
		
	}

	// TODO: promeniti da se stvarno salje mejl autoru, trenutno je zakucano da se meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.
	/**
	 * Kreira se email poruka i salje korisnicima.
	 * @param author - autor kojem se salje mejl
	 * @param paperName - naziv rada koji je potrebno izmeniti
	 * @param deadline - rok za izmenu rada
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(Customer author, String paperName, String deadline, String executionId) throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
//		String authorMail = author.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Rok za izmenu rada";
		// String confirmationUrl = "http://localhost:8080/register/confirmRegistration/" + executionId + "?token=" + token;
		String message = "<html><body>Rad <b>" + paperName + "</b> " + " je potrebno izmeniti.<br> Rok za izmenu: <i>" + deadline + "</i> </body></html>";
		// emailService.sendNotificaitionAsync(authorMail, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}
	
}
