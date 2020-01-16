package rs.ac.uns.ftn.upp.upp.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import rs.ac.uns.ftn.upp.upp.dto.TokenValidationResponse;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.CustomUserDetailsService;

public class AuthenticationFilter extends OncePerRequestFilter {
	private CustomUserDetailsService customUserDetailsService;
	
	public AuthenticationFilter(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
			
			TokenValidationResponse tokenValidationResponse = customUserDetailsService.validateToken(request);
			// if token is valid add data

			if(tokenValidationResponse.getUsername() != null) {
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(tokenValidationResponse.getUsername());
				AuthenticationData authentication = new AuthenticationData(userDetails);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			filterChain.doFilter(request, response);

	}

}
