package rs.ac.uns.ftn.upp.upp.dto.journal;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.MembershipFeeMethod;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;

@Getter
@Setter
@NoArgsConstructor
public class JournalDTO {
	
	private Integer id;

	private String name;

	private String issn;

	private Set<AcademicField> academicFields;

	private MembershipFeeMethod membershipFeeMethod;

	private Double price;

	private Customer editorInChief; // glavni urednik

	private Set<PaperDTO> papers; // radovi

	public JournalDTO(Journal journal) {
		this.id = journal.getId();
		this.name = journal.getName();
		this.issn = journal.getIssn();
		this.academicFields = journal.getJournalAcademicFields();
		System.out.println("naucne oblasti " + journal.getJournalAcademicFields() );
		this.membershipFeeMethod = journal.getMembershipFeeMethod();
		this.price = journal.getPrice();
		this.editorInChief = journal.getEditorInChief();
		this.papers = new HashSet<>();
		if(journal.getPapers() != null) {
			for(Paper paper: journal.getPapers()) {
				this.papers.add(new PaperDTO(paper));
			}
		}
	}
}
