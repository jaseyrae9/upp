package rs.ac.uns.ftn.upp.upp.service.entityservice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.Journal;
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
}
