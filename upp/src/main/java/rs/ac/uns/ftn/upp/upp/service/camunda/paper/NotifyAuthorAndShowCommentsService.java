package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.Review;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.emailservice.EmailService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class NotifyAuthorAndShowCommentsService implements JavaDelegate {

	@Autowired
	private PaperService paperService;
		
	@Autowired
	private EmailService emailService;
	
	/**
	 * Obavestavamo autora rada da je potrebno da ga izmeni u zadatom roku.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u NotifyAuthorAndShowCommentsService");
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		// Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		Customer author = paper.getAuthor();
		String authorMail = author.getEmail();
		String executionId = execution.getId();

		Set<Review> reviews = paper.getReviews();
		Set<String> comments = new HashSet<String>();
		for(Review review: reviews) {
			comments.add(review.getCommentForAuthor());
		}
		
		String deadline = (String) execution.getVariable("noviRok");

		// poslati mejl autoru rada 
		// TODO: skini komentar
		sendEmail(authorMail, paper.getName(), comments, deadline, executionId);
		System.err.println("izasao iz NotifyAuthorAndShowCommentsService");
		
	}

	// TODO: promeniti da se stvarno salje mejl autoru, trenutno je zakucano da se meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.

	/**
	 * Kreira se email poruka i salje korisnicima.
	 * @param author
	 * @param paperName
	 * @param comments
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(String authorMail, String paperName, Set<String> comments, String deadline, String executionId) throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
//		String authorMail = author.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Potrebna izmena rada";
		String message = "<html><body>Rad <b>" + paperName + "</b> " + " je potrebno izmeniti.<br>Rok za izmenu: " + deadline +".<br> Komentari od recenzenata: <i>"+ comments+ "</i> </body></html>";
		// emailService.sendNotificaitionAsync(authorMail, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}
	
}
