package rs.ac.uns.ftn.upp.upp.service.entityservice.journal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.PaperText;
import rs.ac.uns.ftn.upp.upp.repository.PaperTextRepository;

@Service
public class PaperTextService {
	
	@Autowired
	private PaperTextRepository paperTextRepository;
	
	public PaperText getPaperText(Integer id) throws NotFoundException { 
		Optional<PaperText> paper = paperTextRepository.findById(id);
		
		if(!paper.isPresent()) {
			throw new NotFoundException(id, Paper.class.getSimpleName());
		}
		
		return paper.get();
	}
	
	public Optional<PaperText> findById(Integer id) {
		return paperTextRepository.findById(id);
	}	
		
	public PaperText savePaperText(PaperText paperText) {
		return paperTextRepository.save(paperText);
	}

}
