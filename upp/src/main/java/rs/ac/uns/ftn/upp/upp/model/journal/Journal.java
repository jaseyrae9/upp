package rs.ac.uns.ftn.upp.upp.model.journal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.Buyer;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;

@Entity
//Lambok annotations
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Journal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4460840077597039621L;

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String issn;

	@ManyToMany(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	private Set<AcademicField> academicFields;

	@Column(nullable = false)
	private MembershipFeeMethod membershipFeeMethod;

	@Column(nullable = false)
	private Boolean active = false;
	
	@Column()
	private Double price;

	@OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "editor_in_chief_id")
	private Customer editorInChief; // glavni urednik

	@JsonManagedReference(value = "journal")
	@OneToMany(mappedBy = "journal", fetch = FetchType.EAGER)
	private Set<Customer> editors; // ostali urednici

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "journal_reviewers", joinColumns = @JoinColumn(name = "journal_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Getter(AccessLevel.NONE)
	private Set<Customer> reviewers;
	
	public Set<Customer> getJournalReviewers() {
		if (reviewers == null) {
			reviewers = new HashSet<Customer>();
		}
		return reviewers;
	}
	
//	@JsonManagedReference(value = "paper")
//	@OneToMany(mappedBy = "journal", fetch = FetchType.EAGER)
//	private Set<Paper> papers; // radovi
	
	@JsonManagedReference(value = "edition")
	@OneToMany(mappedBy = "journal", fetch = FetchType.EAGER)
	@Getter(AccessLevel.NONE)
	private Set<Edition> editions; // izdanja casopisa, izdanje ima radove

	public Set<Edition> getJournalEditions() {
		if (editions == null) {
			editions = new HashSet<Edition>();
		}
		return editions;
	}
	
	public Set<AcademicField> getJournalAcademicFields() {
		if (academicFields == null) {
			academicFields = new HashSet<AcademicField>();
		}
		return academicFields;
	}
	
	@ManyToMany(mappedBy = "journals", fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	private List<Buyer> buyers;
	
	
	public List<Buyer> getJournalBuyers() {
		if (buyers == null) {
			buyers = new ArrayList<Buyer>();
		}
		return buyers;
	}
	
	

}
