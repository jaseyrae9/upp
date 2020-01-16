package rs.ac.uns.ftn.upp.upp.repository.user.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.user.security.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{

	Optional<VerificationToken> findByToken(String token);
}
