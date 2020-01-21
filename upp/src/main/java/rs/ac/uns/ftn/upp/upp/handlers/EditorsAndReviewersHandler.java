package rs.ac.uns.ftn.upp.upp.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.repository.AcademicFieldRepository;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.UserService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class EditorsAndReviewersHandler implements TaskListener {
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private JournalService journalService;

	// izvlacimo usere koje postoje u bazi
	public void notify(DelegateTask delegateTask) {
		System.err.println("AAAAAAAAAAAAAAAAA Kreiran task za popunjavanje urednika i recenzenata naucnih oblasti");
		
		String starter = (String) delegateTask.getVariable("pokretac");
		System.err.println("pokretac " + starter);
				
		// pronalazim naucne oblasti casopisa
		Set<AcademicField> afs = new HashSet<AcademicField>();
		List<FormSubmissionDTO> form = (List<FormSubmissionDTO>) delegateTask.getVariable("casopis");
		for(FormSubmissionDTO formField: form) {
			if (formField.getFieldId().equals("naziv")) {
				String journalName = (String) formField.getFieldValue();
				
				Optional<Journal> journal = journalService.findJournalByName(journalName);
				if(!journal.isPresent()) {
					System.err.println("nema casopisa sa imenom: " + journalName);
				}
				afs.addAll(journal.get().getJournalAcademicFields());
			}
		}
		System.err.println("afs.size() " + afs.size());
		
		/////////////////////////////////////////////////////
		
		// pronalaze se editori
		System.err.println("heeej: ");
		Authority editorAuthority = authorityService.findByName("EDITOR");
		Authority reviewerAuthority = authorityService.findByName("REVIEWER");

		Set<Customer> editors = new HashSet<Customer>();
		Set<Customer> reviewers = new HashSet<Customer>();

		Iterable<Customer> users = customerService.findAll();
		for(Customer user: users) {
			if(user.getUserAuthorities().contains(editorAuthority))	{
				if(!user.getUsername().equals(starter)) {
					if(user.getJournal() == null) {
						if(user.getCustomerAcademicFields().stream().anyMatch(afs::contains)) {
							System.out.println("DODAJE SE EDITOR U LISTU");
							editors.add((Customer) user);
						}
					}
				}				
			}
			if(user.getUserAuthorities().contains(reviewerAuthority))	{
				if (user.getCustomerAcademicFields().stream().anyMatch(afs::contains)) {
					System.out.println("DODAJE SE REVIEWER U LISTU");

					reviewers.add((Customer) user);
				}
				
			}
			
		}
		
		System.out.println("editors.size(): " + editors.size());
		System.out.println("reviewers.size(): " + reviewers.size());
		
		// da napunim polja
		
		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());
		List<FormField> formFields = tfd.getFormFields();
		Map<String, String> editorsItems = new HashMap<>();
		Map<String, String> reviewersItems = new HashMap<>();

		if(!formFields.isEmpty()) {
			System.err.println("prvi if");
			for(FormField field : formFields) {
				System.err.println("field " + field.getId());
				if(field.getId().equals("uredniciNaucnihOblasti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					editorsItems = eft.getValues();
					editorsItems.clear(); // Praznimo mapu jer nekako vuce stare vrednosti

					System.out.println("Broj editora pre nego sto dodamo nase:" + editorsItems.size());
					System.err.println("editori drugi if ");
					for(Customer editor: editors) {
						System.out.println("editor dodato polje: " + editor.getId() + " vrednost: " + editor.getUsername());
						editorsItems.put(editor.getId().toString(), editor.getUsername());
					}
				}
				if(field.getId().equals("recenzentiNaucnihOblasti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					reviewersItems = eft.getValues();
					reviewersItems.clear();
					System.err.println("recenzenti drugi if");
					for(Customer reviewer: reviewers) {
						System.out.println("reviewer dodato polje: " + reviewer.getId() + " vrednost: " + reviewer.getUsername());
						reviewersItems.put(reviewer.getId().toString(), reviewer.getUsername());
					}
				}
			}
		}	

	}
}