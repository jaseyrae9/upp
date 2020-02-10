package rs.ac.uns.ftn.upp.upp.service.entityservice.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.Coauthor;
import rs.ac.uns.ftn.upp.upp.repository.user.CoauthorRepository;

@Service
public class CoauthorService {

	@Autowired
	private CoauthorRepository coauthorRepository;
		
	public Coauthor saveCoauthor(Coauthor coauthor) {
		System.err.println("cuvamo Coauthor");

		return coauthorRepository.save(coauthor);
	}
	
	public Iterable<Coauthor> findAll() {
		return coauthorRepository.findAll();
	}
	
	public Coauthor findByFirstname(String name) {
		Coauthor ret = null;
		Optional<Coauthor> coauthor = coauthorRepository.findByName(name);
		if(coauthor.isPresent()) {
			ret = coauthor.get();
		}
		return ret;
	}
	
	public Coauthor findById(Integer id) {
		Coauthor ret = null;
		Optional<Coauthor> coauthor = coauthorRepository.findById(id);
		if(coauthor.isPresent()) {
			ret = coauthor.get();
		}
		return ret;
	}
}
