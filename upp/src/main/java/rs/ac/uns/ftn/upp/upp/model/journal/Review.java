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

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;

@Entity
//Lambok annotations
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6138057372916108016L;
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column()
	private String commentForAuthor;	

	@Column()
	private String commentForEditor;
	
	//recommendation
	@Column()
	private Recommendation recommendation;
	
	@JsonBackReference(value="paper")
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="paper_id", referencedColumnName="id")
	private Paper paper;  // na koji rad se odnosi
	
	@JsonBackReference(value="customer")
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="user_id", referencedColumnName="id")
	private Customer reviewer;  // ko je kreirao recenziju
	
	
	

}
