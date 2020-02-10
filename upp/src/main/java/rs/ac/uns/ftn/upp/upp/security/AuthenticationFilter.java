package rs.ac.uns.ftn.upp.upp.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import rs.ac.uns.ftn.upp.upp.dto.TokenValidationResponse;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.CustomUserDetailsService;

public class AuthenticationFilter extends OncePerRequestFilter {
	private CustomUserDetailsService customUserDetailsService;
	private IdentityService identityService;

	public AuthenticationFilter(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (identityService == null) {
			ServletContext servletContext = request.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getWebApplicationContext(servletContext);
			identityService = webApplicationContext.getBean(IdentityService.class);
		}
		TokenValidationResponse tokenValidationResponse = customUserDetailsService.validateToken(request);
		// if token is valid add data

		if (tokenValidationResponse.getUsername() != null) {
			String username = tokenValidationResponse.getUsername();
			UserDetails userDetails = customUserDetailsService
					.loadUserByUsername(tokenValidationResponse.getUsername());
			AuthenticationData authentication = new AuthenticationData(userDetails);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		
			List<Group>groups=identityService.createGroupQuery().groupMember(username).list();
			List<String>groupsIds=new ArrayList<String>();
			for(Group g:groups) {
				System.out.println("grupe: " + g.getName());
				groupsIds.add(g.getId());
			}
			Authentication a=new Authentication(username, groupsIds);
			identityService.setAuthentication(a);
			identityService.setAuthenticatedUserId(username);
		
		
		}
		else {
			authentificateGuest();
		}

		filterChain.doFilter(request, response);

	}
	
	private void authentificateGuest() {
		List<String>gosti=new ArrayList<>();
		gosti.add("gosti");
		Authentication a=new Authentication("gost", gosti);
		identityService.setAuthentication(a);
		identityService.setAuthenticatedUserId("gost");
	}

}
