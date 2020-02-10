package rs.ac.uns.ftn.upp.upp.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.user.Coauthor;

public interface CoauthorRepository extends JpaRepository<Coauthor, Integer> {
	Optional<Coauthor> findByName(String name);	
}
