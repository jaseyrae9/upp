package rs.ac.uns.ftn.upp.upp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.AcademicField;

public interface AcademicFieldRepository extends JpaRepository<AcademicField, Integer> {
	Optional<AcademicField> findByName(String name);	
}
