package rs.ac.uns.ftn.upp.upp.model.user;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse implements Serializable {
	private static final long serialVersionUID = -6624726180748515507L;
	private String token;
	
	
	public AuthenticationResponse(String token) {
		this.setToken(token);
	}
}
