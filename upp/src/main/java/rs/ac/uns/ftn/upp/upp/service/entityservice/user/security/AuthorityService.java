package rs.ac.uns.ftn.upp.upp.service.entityservice.user.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.repository.user.security.AuthorityRepository;

@Service
public class AuthorityService {
	@Autowired
	private AuthorityRepository authorityRepository;
	
	/**
	 * Finds {@link Authority}. named @param name. If no authority with that name is found, creates
	 * new authority.
	 * @param name
	 * @return
	 */
	public Authority findByName(String name) {
		Authority ret = null;
		Optional<Authority> authority = authorityRepository.findByName(name);
		if(authority.isPresent()) {
			ret = authority.get();
		}
		else {
			ret = saveAuthority(name);
		}
		return ret;
	}
	
	private Authority saveAuthority(String name) {
		Authority authority = new Authority();
		authority.setName(name);
		return authorityRepository.save(authority);
	}
}
