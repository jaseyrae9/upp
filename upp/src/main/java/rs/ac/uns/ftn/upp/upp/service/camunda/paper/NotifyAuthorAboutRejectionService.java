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
public class NotifyAuthorAboutRejectionService implements JavaDelegate {

	@Autowired
	private PaperService paperService;
		
	@Autowired
	private EmailService emailService;
	
	/**
	 * Obavestavamo autora rada da je rad odbijen. 
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis za notifikaciju autora o odbijanju rada");
			
		Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		Customer author = paper.getAuthor();
		String executionId = execution.getId();
		String explanation = (String) execution.getVariable("komenatUrednika");

		// poslati mejl i autoru rada 
		// TODO: skini komentar

		//sendEmail(author, paper.getName(), explanation, executionId);
		paper.setAccepted(false);
		paperService.savePaper(paper);
		System.err.println("izasao iz servisa za obavestavanje autora o odbijanju rada.");
		
	}

	// TODO: promeniti da se stvarno salje mejl autoru, trenutno je zakucano da se meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.
	/**
	 * Kreira se email poruka i salje korisnicima.
	 * @param author - autor kojem se salje mejl
	 * @param paperName - naziv rada koji se odbija
	 * @param explanation - razlog za odbijanje 
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(Customer author, String paperName, String explanation, String executionId) throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
//		String authorMail = editorInChief.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Obrazloženje o odbijanju rada";
		// String confirmationUrl = "http://localhost:8080/register/confirmRegistration/" + executionId + "?token=" + token;
		String message = "<html><body>Rad <b>" + paperName + "</b> " + " je odbijen.<br> Razlog urednika: <i>" + explanation + "</i> </body></html>";
		// emailService.sendNotificaitionAsync(authorMail, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}
	
}
