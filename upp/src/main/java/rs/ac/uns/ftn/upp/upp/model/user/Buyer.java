package rs.ac.uns.ftn.upp.upp.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.order.Order;


@Entity
@DiscriminatorValue("buyer")
//Lambok annotations
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Buyer extends MyUser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2124038404023332631L;

	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String country;
	
	@Column()
	private String title;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@JoinTable(name = "buyers_journal", joinColumns = @JoinColumn(name = "buyer_id"), inverseJoinColumns = @JoinColumn(name = "journal_id"))
	private List<Journal> journals;	// casopisi koje je kupio
	
	public List<Journal> getPurchasedJournals() {
		if (journals == null) {
			journals = new ArrayList<Journal>();
		}
		return journals;
	}
	
	@JsonManagedReference(value = "order")
	@OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
	private Set<Order> orders; 
}
