package rs.ac.uns.ftn.upp.upp.model.journal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
//Lambok annotations
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Edition implements Serializable {

	private static final long serialVersionUID = -6781701625134026326L;

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column()
	private Integer number;

	@Column
	private DateTime date;

	@Column()
	private Boolean published = false;

	@JsonBackReference(value = "edition")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "journal_id", referencedColumnName = "id")
	private Journal journal; // kom casopisu pripada izdanje

	@JsonManagedReference(value = "paper")
	@OneToMany(mappedBy = "edition", fetch = FetchType.EAGER)
	private Set<Paper> papers; // radovi izdanja
	
	@Column()
	private Integer ticks;
	
	public Edition(Edition edition) {
		super();
		this.id = edition.getId();
		this.number = edition.getNumber();
		this.date = edition.getDate();
		this.published = edition.getPublished();
		this.journal = edition.getJournal();
		this.ticks = edition.getTicks();
		this.papers = new HashSet<>();
		if(edition.getPapers() != null) {
			for(Paper paper: edition.getPapers()) {
				this.papers.add(paper);
			}
		}
	}

}
