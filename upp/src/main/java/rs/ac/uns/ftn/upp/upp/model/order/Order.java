package rs.ac.uns.ftn.upp.upp.model.order;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.user.Buyer;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "orders")
public class Order {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private Double price;
	
	@Column
	private String callbackUrl;
	
	@Column
	private OrderStatus status;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	protected List<Journal> journals;
	
	public List<Journal> getOrderJournals() {
		if(journals == null) {
			journals = new ArrayList<>();
		}
		return journals;
	}	
	
	@JsonBackReference(value="order")
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="buyer_id", referencedColumnName="id")
	private Buyer buyer; 
}
