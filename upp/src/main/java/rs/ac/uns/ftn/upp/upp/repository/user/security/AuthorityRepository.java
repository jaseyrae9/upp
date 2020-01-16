package rs.ac.uns.ftn.upp.upp.repository.user.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer>{
	Optional<Authority> findByName(String name);

}
