package rs.ac.uns.ftn.upp.upp.model.user;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("admin")
//Lambok annotations
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Admin extends MyUser implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -4567134673473363578L;

}
