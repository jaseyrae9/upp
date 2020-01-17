package rs.ac.uns.ftn.upp.upp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.Journal;

public interface JournalRepository  extends JpaRepository<Journal, Integer> {
	Optional<Journal> findByName(String name);	

}