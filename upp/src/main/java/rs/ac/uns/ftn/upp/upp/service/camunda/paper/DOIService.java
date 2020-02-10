package rs.ac.uns.ftn.upp.upp.service.camunda.paper;

import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class DOIService implements JavaDelegate {

	@Autowired
	private PaperService paperService;

	/**
	 * Dodala DOI-a radu.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.err.println("usao u servis dodelu DOI-a radu.");
		
		Integer paperId = Integer.parseInt(String.valueOf(execution.getVariable("radId")));

		// Integer paperId = (Integer) execution.getVariable("radId");
		Optional<Paper> paperOpt = paperService.findById(paperId);
		if(!paperOpt.isPresent()) {
			throw new NotFoundException(paperId, Paper.class.getSimpleName());
		}
		Paper paper = paperOpt.get();
		String firstPart = "http://dx.doi.org/10.";
		Random rand = new Random(); 
		String first = Integer.toString(rand.nextInt(10)); 
		String second = Integer.toString(rand.nextInt(10));  
		String third = Integer.toString(rand.nextInt(10)); 
		String fourth = Integer.toString(rand.nextInt(10)); 
		String fourNumbers = first + second + third + fourth + "/";
		
		Integer randomSize = rand.nextInt(30);
		
		String lastPart = RandomStringUtils.randomAlphanumeric(randomSize);
		String doi = firstPart + fourNumbers + lastPart;
		System.out.println("doi " + doi);
		paper.setDoi(doi);
		paperService.savePaper(paper);
	
		System.err.println("izasao iz servisa za dodelu DOI-a radu.");
		
	}

	
}
