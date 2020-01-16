package rs.ac.uns.ftn.upp.upp.security.auth;

import javax.validation.constraints.NotBlank;

/**
 * Class used to receive user login in request.
 *
 */
public class JwtAuthenticationRequest {
	@NotBlank (message = "Please, enter your username.")
    private String username;
	
	@NotBlank (message = "Please, enter your password.")
    private String password;

    public JwtAuthenticationRequest() {
        super();
    }

    public JwtAuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
