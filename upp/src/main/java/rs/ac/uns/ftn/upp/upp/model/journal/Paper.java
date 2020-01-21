package rs.ac.uns.ftn.upp.upp.model.journal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
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

	@OneToOne(targetEntity = AcademicField.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "academicField_id")
	private AcademicField academicField; // Naučna oblast u koju se rad primarno klasifikuje 
	
	@Column(nullable = false)
	private Double price;
	
	@JsonBackReference(value="paper")
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="journal_id", referencedColumnName="id")
	private Journal journal; 

	@OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "author_id")
	private Customer author; // autor rada
	
}
