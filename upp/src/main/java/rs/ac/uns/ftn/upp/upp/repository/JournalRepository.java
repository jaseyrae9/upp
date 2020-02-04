package rs.ac.uns.ftn.upp.upp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.journal.Journal;

public interface JournalRepository  extends JpaRepository<Journal, Integer> {
	Optional<Journal> findByName(String name);	
	List<Journal> findAllByActive(Boolean active);

}
