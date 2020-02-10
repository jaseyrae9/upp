package rs.ac.uns.ftn.upp.upp.service.entityservice.journal;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.repository.EditionRepository;

@Service
public class EditionService {
	@Autowired
	private EditionRepository editionRepository;

	public Edition findByNumber(Integer number) {
		Edition ret = null;
		Optional<Edition> edition = editionRepository.findByNumber(number);
		if (edition.isPresent()) {
			ret = edition.get();
		}
		return ret;
	}

	public Edition getEdition(Integer id) throws NotFoundException {
		Optional<Edition> edition = editionRepository.findById(id);

		if (!edition.isPresent()) {
			throw new NotFoundException(id, Edition.class.getSimpleName());
		}

		return edition.get();
	}

	public Edition findByIsPublished(Boolean published) throws NotFoundException, RequestDataException {
		Optional<Edition> edition = editionRepository.findByPublished(published);

		if (!edition.isPresent()) {
			throw new RequestDataException(
					"Ne postoji izdanje koje je: " + (published == true) != null ? "objavljeno" : "ne objavljeno");
		}

		return edition.get();
	}

	public Optional<Edition> findById(Integer id) {
		return editionRepository.findById(id);
	}

	public Set<Edition> findAllByPublished(Boolean published) {
		return editionRepository.findAllByPublished(published);
	}

	public Edition saveEdition(Edition edition) {
		return editionRepository.save(edition);
	}

}
