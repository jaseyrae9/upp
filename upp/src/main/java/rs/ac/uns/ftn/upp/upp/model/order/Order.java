package rs.ac.uns.ftn.upp.upp.model.order;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;

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
	
	@ManyToMany(fetch = FetchType.EAGER)
	@Getter(AccessLevel.NONE)
	protected List<Paper> papers;
	
	public List<Paper> getOrderPapers() {
		if(papers == null) {
			papers = new ArrayList<>();
		}
		return papers;
	}	
	
}
