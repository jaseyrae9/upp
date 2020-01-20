package rs.ac.uns.ftn.upp.upp.model.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;


@Entity
@DiscriminatorValue("customer")
//Lambok annotations
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Customer extends MyUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896607559439752938L;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String country;
	
	@Column()
	private String title;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	private Set<AcademicField> academicFields;	
	
	public Set<AcademicField> getCustomerAcademicFields() {
		if(academicFields == null) {
			academicFields = new HashSet<AcademicField>();
		}
		return academicFields;
	}	

	@Column(nullable = false)
	private Boolean wantsToBeReviewer;
	
	@Column(nullable = false)
	private Boolean acceptedAsReviewer = false;
	
	@Column(nullable = false)
	private Boolean active = false;

	@JsonBackReference(value="journal")
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="journal_id", referencedColumnName="id")
	private Journal journal;  // Kada je urednik (U casopisu se pamti ko je glavni urednik)
	
	@ManyToMany(mappedBy = "reviewers", fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	private Set<Journal> journals;	// Kada je recenzent
	
	public Set<Journal> getCustomerJournals() {
		if(journals == null) {
			journals = new HashSet<Journal>();
		}
		return journals;
	}	
}
