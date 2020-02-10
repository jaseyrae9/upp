package rs.ac.uns.ftn.upp.upp.repository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.journal.Edition;

public interface EditionRepository  extends JpaRepository<Edition, Integer> {
	Optional<Edition> findByNumber(Integer number);	
	Optional<Edition> findByDate(Date date);	
	Optional<Edition> findByPublished(Boolean published);	
	Set<Edition> findAllByPublished(Boolean published);

}
