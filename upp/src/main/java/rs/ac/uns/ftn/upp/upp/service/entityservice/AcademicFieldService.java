package rs.ac.uns.ftn.upp.upp.service.entityservice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.repository.AcademicFieldRepository;

@Service
public class AcademicFieldService {
	
	@Autowired
	private AcademicFieldRepository academicFieldRepository;
	
	public AcademicField findByName(String name) {
		AcademicField ret = null;
		Optional<AcademicField> academicField = academicFieldRepository.findByName(name);
		if(academicField.isPresent()) {
			ret = academicField.get();
		}
		return ret;
	}
}
