package rs.ac.uns.ftn.upp.upp.dto.journal;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;

@Getter
@Setter
@NoArgsConstructor
public class EditionDTO {
	
	private Integer id;
	private Integer number;
	private DateTime date;
	private Boolean published = false;
	private JournalDTO journal;
	private Set<PaperDTO> papers;


	public EditionDTO(Edition edition) {
		this.id = edition.getId();
		this.number = edition.getNumber();
		this.date = edition.getDate();
		this.published = edition.getPublished();
		this.papers = new HashSet<>();
		if(edition.getPapers() != null) {
			for(Paper paper: edition.getPapers()) {
				this.papers.add(new PaperDTO(paper));
			}
		}
	}
}
