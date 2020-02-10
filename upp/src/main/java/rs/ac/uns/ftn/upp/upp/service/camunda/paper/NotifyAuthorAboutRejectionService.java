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
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		//Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		paper.setAccepted(false);
		paperService.savePaper(paper);
		String explanation = "";
		if(!paper.getIsThematicallyAcceptable()) {
			System.err.println("usao u if da nije tematski prihvatljiv");
			explanation = (String) execution.getVariable("komenatUrednika");
		}
		Boolean isExpired = Boolean.parseBoolean((String) execution.getVariable("istekloVreme"));
		if(isExpired) {
			explanation = "Isteklo je vreme.";
			System.err.println("usao u if da je isteklo vreme");

		}
		
		//odlukaUrednika
		String odlukaUrednika = null;		
		try {
			odlukaUrednika = (String) execution.getVariable("odlukaUrednika");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(odlukaUrednika != null) {
			if(odlukaUrednika.contentEquals("odbiti")) {
				System.err.println("usao u if odlukaUrednika = odbiti");

				explanation = "Nakon pregledanja recenzija odluceno je da se rad odbija.";

			}
		}

		Customer author = paper.getAuthor();
		String authorMail = author.getEmail();
		String executionId = execution.getId();
		System.out.println("explanation " + explanation);
		// poslati mejl i autoru rada 
		// TODO: skini komentar
		sendEmail(authorMail, paper.getName(), explanation, executionId);
		
		System.err.println("izasao iz servisa za obavestavanje autora o odbijanju rada.");
		
	}

	// TODO: promeniti da se stvarno salje mejl autoru, trenutno je zakucano da se meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.
	/**
	 * Kreira se email poruka i salje korisnicima.
	 * @param authorMail - mejl autora kojem se salje mejl
	 * @param paperName - naziv rada koji se odbija
	 * @param explanation - razlog za odbijanje 
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(String authorMail, String paperName, String explanation, String executionId) throws MessagingException, MailException, InterruptedException {
		System.err.println("usao u metodu za mejl");
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Obrazloženje o odbijanju rada";
		String message = "<html><body>Rad <b>" + paperName + "</b> " + " je odbijen.<br> Razlog urednika: <i>" + explanation + "</i> </body></html>";
		// emailService.sendNotificaitionAsync(authorMail, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}
	
}
