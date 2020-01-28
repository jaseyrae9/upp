package rs.ac.uns.ftn.upp.upp.dto.journal;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.MembershipFeeMethod;

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

	private Set<Paper> papers; // radovi

	public JournalDTO(Journal journal) {
		this.id = journal.getId();
		this.name = journal.getName();
		this.issn = journal.getIssn();
		this.academicFields = journal.getJournalAcademicFields();
		this.membershipFeeMethod = journal.getMembershipFeeMethod();
		this.price = journal.getPrice();
		this.editorInChief = journal.getEditorInChief();
		this.papers = journal.getPapers();
	}
}
