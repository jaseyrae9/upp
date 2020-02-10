package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.emailservice.EmailService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class SystemChoosesAcademicFieldEditorService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private CustomerService customerService;
	// sistem bira urednika naucne oblasti za koju je rad prijavljen i salje mu
	// notifikaciju o novom radu
	// ako casopis nema aktivnog urednika naucne oblasti, posao se dodeljuje glavnom
	// uredniku
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u service za odabir urednika naucne oblasti za koju je rad prijavljen blabla");

		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if (!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		AcademicField paperAcademicField = paper.getAcademicField();
		Edition edition = paper.getEdition(); // izdanje kojem pripada rad
		Journal journal = edition.getJournal(); // preko izdanja kojem pripada rad dobijemo casopis
		Customer editorInChief = journal.getEditorInChief();
		Set<Customer> journalEditors = journal.getEditors();
		journalEditors.remove(editorInChief);
		System.err.println("journalEditors.size() " + journalEditors.size());
		List<Customer> editors = new ArrayList<Customer>();
		String userRezultat = "";
		if (journalEditors.isEmpty()) {
			System.err.println("ne postoje urednici u casopisu " + journal.getName());
			userRezultat = editorInChief.getUsername();
			execution.setVariable("userRezultat", userRezultat);
		} else {
			System.err.println("postoje urednici u casopisu " + journal.getName());
			for (Customer journalEditor : journalEditors) {
				if (journalEditor.getCustomerAcademicFields().contains(paperAcademicField)) {
					System.err.println("ima ih bas od te naucne oblasti");
					editors.add(journalEditor);
				}
			}
			if (!editors.isEmpty()) {
				for (Customer e : editors) {
					System.err.println("pre editors: " + e.getUsername());
				}
				Collections.shuffle(editors);
				for (Customer e : editors) {
					System.err.println("posle editors: " + e.getUsername());
				}
				userRezultat = editors.stream().findFirst().get().getUsername();

				execution.setVariable("userRezultat", userRezultat);
			} else {
				userRezultat = editorInChief.getUsername();
				execution.setVariable("userRezultat", userRezultat);
			}
		}

		// poslati mejl izabranom korisniku
		// TODO: skini komentar
		Customer choosen = customerService.findByUsername(userRezultat);
		String executionId = execution.getId();
		sendEmail(choosen, paper.getName(), executionId);
		System.err.println("izasao iz servisa za odabir urednika naucne oblasti za koju je rad prijavljen blabla");


	}

	// TODO: promeniti da se stvarno salje mejl autoru, trenutno je zakucano da se
	// meni salje mejl da ne bih morala
	// da menjam na prave mejlove u bazi.
	/**
	 * Kreira se email poruka i salje korisnicima.
	 * 
	 * @param author      - autor kojem se salje mejl
	 * @param paperName   - naziv rada koji je potrebno izmeniti
	 * @param deadline    - rok za izmenu rada
	 * @param executionId
	 * @throws MessagingException
	 * @throws MailException
	 * @throws InterruptedException
	 */
	private void sendEmail(Customer choosen, String paperName, String executionId)
			throws MessagingException, MailException, InterruptedException {
		// Token
		System.err.println("usao u metodu za mejl");
//		String choosenMail = choosen.getEmail();
		String recipientMail = "jaseyraee9@gmail.com";
		String subject = "Notifikacija uredniku naučne oblasti o novom radu";
		// String confirmationUrl =
		// "http://localhost:8080/register/confirmRegistration/" + executionId +
		// "?token=" + token;
		String message = "<html><body>Novi rad: <b>" + paperName + "</b> " + "<br> </body></html>";
		// emailService.sendNotificaitionAsync(choosenMail, subject, message);
		emailService.sendNotificaitionAsync(recipientMail, subject, message);

	}

}
