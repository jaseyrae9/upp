package rs.ac.uns.ftn.upp.upp.model.user;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.identity.Group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse implements Serializable {
	private static final long serialVersionUID = -6624726180748515507L;
	private String token;
    private List<String> groups;

	
	public AuthenticationResponse(String token, List<Group> groups) {
		this.setToken(token);
		this.groups = groups.stream().map(Group::getId).collect(Collectors.toList());

	}
}
