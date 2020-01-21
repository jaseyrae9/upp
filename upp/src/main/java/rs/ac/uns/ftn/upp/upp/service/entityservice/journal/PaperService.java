package rs.ac.uns.ftn.upp.upp.service.entityservice.journal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.repository.PaperRepository;

@Service
public class PaperService {
	
	@Autowired
	private PaperRepository paperRepository;
	
	public Paper findByName(String name) {
		Paper ret = null;
		Optional<Paper> paper = paperRepository.findByName(name);
		if(paper.isPresent()) {
			ret = paper.get();
		}
		return ret;
	}
	
	public Paper getPaper(Integer id) throws NotFoundException { 
		Optional<Paper> paper = paperRepository.findById(id);
		
		if(!paper.isPresent()) {
			throw new NotFoundException(id, Paper.class.getSimpleName());
		}
		
		return paper.get();
	}
	
	public Optional<Paper> findById(Integer id) {
		return paperRepository.findById(id);
	}
	
	public Optional<Paper> findPaperByName(String name) {
		return paperRepository.findByName(name);
	}
	
	public Paper savePaper(Paper paper) {
		return paperRepository.save(paper);
	}

}
