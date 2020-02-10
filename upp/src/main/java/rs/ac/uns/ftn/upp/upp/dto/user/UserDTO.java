package rs.ac.uns.ftn.upp.upp.dto.user;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
	
	private Integer id;

	private String username;
	private String firstName;
	private String lastName;
	private List<Authority> authorities;

	public UserDTO(Customer user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.authorities = user.getUserAuthorities();
	}

}
