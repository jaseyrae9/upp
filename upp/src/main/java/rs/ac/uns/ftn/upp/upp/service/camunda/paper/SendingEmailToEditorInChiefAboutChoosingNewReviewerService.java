package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class SendingEmailToEditorInChiefAboutChoosingNewReviewerService implements JavaDelegate {

	@Autowired
	private PaperService paperService;
	
	@Autowired
	private CustomerService customerService;
		
	@Autowired
	private EmailService emailService;
	
	/**
	 * Obavestava se dodeljeni urednik da je rok istekao recenzentu i da treba da bira novog.
	 * 
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis SendingEmailToEditorInChiefAboutChoosingNewReviewerService");

		String executionId = execution.getId();

		// userRezultat -> kome se salje mejl
		String userRezultat = (String) execution.getVariable("userRezultat");
		System.out.println("userRezultat: " + userRezultat);
		Optional<Customer> opt = customerService.findCustomer(userRezultat);
		if(!opt.isPresent()) {
			throw new NotFoundException(userRezultat, Customer.class.getSimpleName());
		}
		Customer userResult = opt.get();
		
		// recenzent koji nije odradio zadatak
		String oneUserId = (String) execution.getVariable("oneUserId");
		Optional<Customer> optReviewer = customerService.findCustomer(oneUserId);
		if(!optReviewer.isPresent()) {
			throw new NotFoundException(oneUserId, Customer.class.getSimpleName());
		}
		Customer reviewer = optReviewer.get();
		System.out.println("korisnik: " + oneUserId);
		
		Set<Customer> istekli = (HashSet<Customer>) execution.getVariable("istekli");
		istekli.add(reviewer);
		execution.setVariable("istekli", istekli);
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		// Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		
		// izbaci recenzenta iz rada i izbaci rad iz recenzenta
		paper.getPaperReviewers().remove(reviewer);
		reviewer.getReviewerPapers().remove(paper);
		paperService.savePaper(paper);
		customerService.saveCustomer(reviewer);	
		
		sendEmail(userResult, oneUserId, executionId);
		System.err.println("izasao iz servisa SendingEmailToEditorInChiefAboutChoosingNewReviewerService");
		
	}

	// TODO: promeniti da se stvarno njima salje mej, trenutno je zakucano da se meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.
	private void sendEmail(Customer userResult, String reviewer, String executionId) throws MessagingException, MailException, InterruptedException {
		System.err.println("usao u metodu za mejl");
		String token = UUID.randomUUID().toString();
		System.err.println("token: " + token);

//		String userResult = author.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Recenzentu je isteklo vreme, morate izabrati novog";
		String message = "<html><body>Recenzentu " + reviewer + " je isteklo vreme. Potrebno je da izaberete novog.<br></body></html>";
		// emailService.sendNotificaitionAsync(userResult, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}
	
}
