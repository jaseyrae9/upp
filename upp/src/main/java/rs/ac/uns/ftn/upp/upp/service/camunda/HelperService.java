package rs.ac.uns.ftn.upp.upp.service.camunda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.FormSubmissionDTO;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.AcademicFieldService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.JournalService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

@Service
public class HelperService {

	@Autowired
	private PaperService paperService;
	
	@Autowired
	private JournalService journalService;
	
	@Autowired
	private AcademicFieldService academicFieldService;
	
	@Autowired
	private CustomerService customerService;

	@Autowired
	private IdentityService identityService;
	
	public boolean authorize(String groupId) {
		String username = "";
		try {
			username = identityService.getCurrentAuthentication().getUserId();
		} catch (Exception e) {
			return false;
		}
		Group group = identityService.createGroupQuery().groupMember(username).groupId(groupId).singleResult();
		if (group != null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/**
	 * Metoda koja se koristi prilikom prikaza forme za zadatak gde urednik treba da
	 * izabere novog/zamenskog recenzenta. 
	 * Ne treba da ponudi onog sto je vec recenzirao, 
	 * ne treba da ponudi one sto nisu na vreme recenzirali naucna oblast rada.
	 * 
	 * @param properties - polja
	 * @param paperId    - rad za koji se popunjava
	 * @param istekli    - recenzenti koji nisu zavrsili zadatak recenziranja
	 */
	public void fillReviewers(List<FormField> properties, Integer paperId, Set<Customer> istekli) {
		Optional<Paper> opt = paperService.findById(paperId);
		System.err.println("DEBUG FILL DATA");

		System.out.println("istekli.size() " + istekli.size());
		for (Customer i : istekli) {
			System.out.println("istekao: " + i.getUsername());
		}
		if (!opt.isPresent()) {
			System.err.println("Nije pronadjen casopis ID: " + paperId);
			return; // Necemo nista popuniti
		}

		Paper paper = opt.get(); // pronadjen koji rad
		Edition edition = paper.getEdition(); // izdanje kojem pripada rad
		AcademicField paperAcademicField = paper.getAcademicField(); // naucna oblast kojoj pripada rad
		Journal journal = edition.getJournal(); // preko izdanja dolazimo da casopisa (radovi su u izdanju, a casopis ima izdanja)

		// svi recenzenti koji su u tom casopisu
		Set<Customer> journalReviewers = journal.getJournalReviewers();
		Set<Customer> temp = new HashSet<>(); // lista recenzenata koji rade u toj naucnoj oblasti
		Set<Customer> reviewers = new HashSet<>();

		//
		Customer editorInChief = journal.getEditorInChief();
		
		if (journalReviewers.isEmpty()) {
			// TODO: mozda staviti samo glavnog urednika
			System.out.println("ne postoje recenzenti u casopisu " + journal.getName());
			//
			temp.add(editorInChief);
			//return; // Necemo nista popuniti
		} else {
			for (Customer journalReviewer : journalReviewers) {
				// pronaci recenzente koji rade u toj naucnoj oblasti
				if (journalReviewer.getCustomerAcademicFields().contains(paperAcademicField)){
					System.out.println("ima recenzenata bas od te naucne oblasti");
					temp.add(journalReviewer);
				}
			}
			if (temp.isEmpty()) {
				// TODO: mozda staviti samo glavnog urednika
				System.out.println("dodajemo glavnog urednika jer ne postoje recenzenti te naucne oblasti");
				temp.add(editorInChief);

				//return; // Necemo nista popuniti
			}
		}

		// medju recenzentima postoje ovi koji nisu odradili task, izbaci ih
		temp.removeAll(istekli);
		Set<Customer> list = new HashSet<Customer>();
		if (!temp.isEmpty()) {
			System.out.println("nije prazno + " + temp.size());
			for (Customer r : temp) {
				System.out.println("broj njegovih recenzija: " + r.getReviewerPapers().size() + " " + r.getUsername());
				if (!r.getReviews().stream().anyMatch(paper.getReviews()::contains)) {
					System.out.println("usao u ific " + r.getUsername());
					list.add(r);
				}
			}
		}
		
		if(list.isEmpty()) {
			list.add(editorInChief);
		}
		
		Map<String, String> itemsReviewers = new HashMap<>();

		if (!properties.isEmpty()) {
			for (FormField field : properties) {
				System.err.println("field " + field.getId());
				if (field.getId().equals("ponovoIzaberi") || field.getId().equals("ponovoIzaberiRecenzenta")) {
					EnumFormType eft = (EnumFormType) field.getType();
					itemsReviewers = eft.getValues();
					itemsReviewers.clear(); // Praznimo mapu ako ima stari vrednosti
					for (Customer reviewer : list) {
						System.out.println(
								"reviewer dodato polje: " + reviewer.getId() + " vrednost: " + reviewer.getUsername());
						itemsReviewers.put(reviewer.getId().toString(), reviewer.getUsername());
					}
				}
			}
		}

		System.err.println("KRAJ DEBUG FILL DATA");
	}

	public HashMap<String, Object> mapListToDto(List<FormSubmissionDTO> list) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (FormSubmissionDTO temp : list) {
			if (temp.getFieldId().contentEquals("casopisi")) {
				Journal journal = journalService.findByName(temp.getFieldValue().toString());
				map.put(temp.getFieldId(), journal.getId().toString());
			} else if (temp.getFieldId().contentEquals("naucnaOblast")) {
				AcademicField af = academicFieldService.findByName(temp.getFieldValue().toString());
				map.put(temp.getFieldId(), af.getId().toString());
			} else if (temp.getFieldValue() instanceof List) {
				List<String> listTemp = (List<String>) temp.getFieldValue();
				if (!listTemp.isEmpty()) {
					System.err.println("LSITA " + temp.getFieldId());

					Customer c = customerService.findByUsername(listTemp.get(0));
					map.put(temp.getFieldId(), c.getId().toString());
				}
			} else if (temp.getFieldId().contentEquals("preporuka") || temp.getFieldId().contentEquals("odlukaUrednika")) {
				System.err.println("preporuka");
				String preporuka = StringUtils.deleteWhitespace(temp.getFieldValue().toString());
				map.put(temp.getFieldId(), preporuka.toLowerCase());

			} else if (temp.getFieldId().contentEquals("ponovoIzaberi") || temp.getFieldId().contentEquals("ponovoIzaberiRecenzenta")) {
				Customer reviewer = customerService.findByUsername((String) temp.getFieldValue());
				map.put(temp.getFieldId(), reviewer.getId().toString());
			} else if(temp.getFieldId().contentEquals("fileUpload")) {				
				map.put(temp.getFieldId(), ""); //jer je predugo
			}
			else {
				map.put(temp.getFieldId(), temp.getFieldValue());
			}
		}

		return map;
	}

}
