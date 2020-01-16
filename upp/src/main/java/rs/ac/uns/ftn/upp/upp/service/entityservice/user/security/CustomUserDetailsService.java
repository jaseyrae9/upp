package rs.ac.uns.ftn.upp.upp.service.entityservice.user.security;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.TokenValidationResponse;
import rs.ac.uns.ftn.upp.upp.model.user.MyUser;
import rs.ac.uns.ftn.upp.upp.model.user.security.CustomUserDetails;
import rs.ac.uns.ftn.upp.upp.repository.user.MyUserRepository;
import rs.ac.uns.ftn.upp.upp.security.auth.TokenUtils;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private MyUserRepository userRepository;
	
	@Autowired
	private TokenUtils tokenUtils;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<MyUser> optionalUser = userRepository.findByUsername(username);
		optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username " + username  + "not found."));
		return optionalUser.map(CustomUserDetails::new).get();
	}
	
	/**
	 * Checks if token is valid. If token is valid returns respose object with set
	 * username and list of authorites owned by user.
	 * 
	 * @param token
	 * @return object containting information about validity of token and token
	 *         owner
	 */
	public TokenValidationResponse validateToken(HttpServletRequest request) {
		// create new response in which token is marked as invalid
		TokenValidationResponse response = new TokenValidationResponse();
		// remove Bearer keyword
		String token = tokenUtils.getToken(request);
		System.out.println("TOKEN: " + token);
		if (token != null) {
			// find user linked to token
			String username = tokenUtils.getUsernameFromToken(token);
			System.out.println("USERNAME IZ TOKENA: " + username);
			if (username != null) {
				// load user and check if token is valid
				UserDetails userDetails = loadUserByUsername(username);
				if (tokenUtils.validateToken(token, userDetails)) {
					CustomUserDetails customUserDetails = (CustomUserDetails)userDetails;
					// set information
					response.setIsValid(true);
					response.setUserId(customUserDetails.getId());
					response.setUsername(username);
					response.setAuthorities(StringUtils.join(userDetails.getAuthorities(), ','));
				}
			}
		}
		return response;
	}

}
