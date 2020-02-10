package rs.ac.uns.ftn.upp.upp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.PaperText;

public interface PaperTextRepository  extends JpaRepository<PaperText, Integer> {
	

}
