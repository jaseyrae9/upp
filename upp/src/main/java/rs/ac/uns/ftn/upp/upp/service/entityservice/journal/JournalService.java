package rs.ac.uns.ftn.upp.upp.service.entityservice.journal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.repository.JournalRepository;

@Service
public class JournalService {
	
	@Autowired
	private JournalRepository journalRepository;
	
	public Journal findByName(String name) {
		Journal ret = null;
		Optional<Journal> journal = journalRepository.findByName(name);
		if(journal.isPresent()) {
			ret = journal.get();
		}
		return ret;
	}
	public Optional<Journal> findById(Integer id) {
		return journalRepository.findById(id);
	}
	
	public Optional<Journal> findJournalByName(String name) {
		return journalRepository.findByName(name);
	}
	
	public Journal saveJournal(Journal journal) {
		return journalRepository.save(journal);
	}
	
	/**
	 * @return informations about all active journals
	 */
	public Iterable<Journal> getJournals() {
		System.err.println("Usao u get journals servise");

		Iterable<Journal> journals = journalRepository.findAll();
		return journals;
	}
	public Journal getJournal(Integer id) throws NotFoundException {
		Optional<Journal> journal = journalRepository.findById(id);
		if (!journal.isPresent()) {
			throw new NotFoundException(id, Journal.class.getSimpleName());
		}
		System.err.println("service: journal id " + id);
		return journal.get();
	}
}
