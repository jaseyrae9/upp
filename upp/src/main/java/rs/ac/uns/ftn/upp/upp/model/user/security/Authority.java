package rs.ac.uns.ftn.upp.upp.model.user.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

//Database annotations
@Entity
//Lambok annotations
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/**
 * Java class for Authority complex type.
 *
 *
 */
public class Authority implements GrantedAuthority {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String name;

	@Override
	public String getAuthority() {
		return name;
	}

}
