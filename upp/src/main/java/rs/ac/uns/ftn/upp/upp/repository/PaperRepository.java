package rs.ac.uns.ftn.upp.upp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.journal.Paper;

public interface PaperRepository  extends JpaRepository<Paper, Integer> {
	Optional<Paper> findByName(String name);	

}
