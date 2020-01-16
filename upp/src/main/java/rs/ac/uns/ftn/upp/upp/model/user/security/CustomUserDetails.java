package rs.ac.uns.ftn.upp.upp.model.user.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import rs.ac.uns.ftn.upp.upp.model.user.MyUser;

public class CustomUserDetails extends MyUser implements UserDetails {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -963025379223800437L;

	public CustomUserDetails(final MyUser user)
	{
		super(user);
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {	
		return super.getUserAuthorities()
				.stream()
				.map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getAuthority()))
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	public String getUsername() {
		return super.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return super.getConfirmedMail();
	}
}
