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
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class NotifyEdtiorAndShowCommentsAfterReviewsService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private CustomerService customerService;

	/**
	 * Obavestavamo urednika da treba da pregleda izmene.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u NotifyEdtiorAndShowCommentsService");

		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		//Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		String userRezultat = (String) execution.getVariable("userRezultat");
		System.out.println("urednik kojem se salje mejl: " + userRezultat);
		Customer user = customerService.findByUsername(userRezultat);

		String userMail = user.getEmail();
		String executionId = execution.getId();

		Set<Review> reviews = paper.getReviews();
		Set<String> comments = new HashSet<String>();
		for (Review review : reviews) {
			comments.add(review.getCommentForEditor());
		}

		// poslati mejl autoru rada
		// TODO: skini komentar
		sendEmail(userMail, paper.getName(), comments, executionId);
		System.err.println("izasao iz NotifyEdtiorAndShowCommentsService");

	}

	// TODO: promeniti da se stvarno salje mejl autoru, trenutno je zakucano da se
	// meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.

	/**
	 * Kreira se email poruka i salje korisnicima.
	 * 
	 * @param userMail		- mejl korisnika kojem se salje mejl
	 * @param paperName 	- naziv rada koji se ponovo pregleda
	 * @param comments		- komentari recenzenata
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(String userMail, String paperName, Set<String> comments, String executionId)
			throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
//		String userMail = author.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Pristigao recenziran rad";
		String message = "<html><body>Rad <b>" + paperName + "</b> "
				+ " je recenziran. Potrebno je doneti odluku. <br>Komentari od recenzenata: <i>" + comments
				+ "</i> </body></html>";
		// emailService.sendNotificaitionAsync(userMail, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}

}
