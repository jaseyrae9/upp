package rs.ac.uns.ftn.upp.upp.model.journal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.Coauthor;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;

@Entity
//Lambok annotations
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paper implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1468388281048260852L;

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column(nullable = false)
	private String keyTerms; // kljucne reci
	
	@Column(nullable = false)
	private String abstractOfPaper;
	
	@OneToOne(targetEntity = AcademicField.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "academicField_id")
	private AcademicField academicField; // Naučna oblast u koju se rad primarno klasifikuje 

	@Column()
	private Boolean isThematicallyAcceptable = false;
	
	@Column()
	private Boolean accepted;
	
	@Column()
	private String doi;
	
	@OneToOne(targetEntity = PaperText.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinColumn(nullable = false, name = "paper_text_id")
	private PaperText paperText; // tekst rada
	
	@JsonBackReference(value="paper")
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="edition_id", referencedColumnName="id")
	private Edition edition;  // kom izdanju casopisa pripada rad

	@OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "author_id")
	private Customer author; // autor rada
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "paper_reviewers", joinColumns = @JoinColumn(name = "paper_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Getter(AccessLevel.NONE)
	private Set<Customer> paperReviewers; // recenzenti
	
	public Set<Customer> getPaperReviewers() {
		if (paperReviewers == null) {
			paperReviewers = new HashSet<Customer>();
		}
		return paperReviewers;
	}
	
	@JsonManagedReference(value = "paper")
	@OneToMany(mappedBy = "paper", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<Review> reviews; // recenzije
	
	@Column()
	private Decision decision;
	
	@JsonManagedReference(value = "coauthor")
	@OneToMany(mappedBy = "paper", fetch = FetchType.EAGER,  cascade = CascadeType.REMOVE)
	@Getter(AccessLevel.NONE)
	private Set<Coauthor> coauthors; // koautori rada
	
	public Set<Coauthor> getCoauthors() {
		if (coauthors == null) {
			coauthors = new HashSet<Coauthor>();
		}
		return coauthors;
	}
	
}
