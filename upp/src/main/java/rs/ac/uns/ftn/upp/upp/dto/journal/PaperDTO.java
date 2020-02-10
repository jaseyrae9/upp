package rs.ac.uns.ftn.upp.upp.dto.journal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.PaperText;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;

@Getter
@Setter
@NoArgsConstructor
public class PaperDTO {
	
	private Integer id;
	private String name;
	private AcademicField academicField; // Naučna oblast u koju se rad primarno klasifikuje 
	private EditionDTO edition; 
	private Customer author; // autor rada
	
	public PaperDTO(Paper paper) {
		this.id = paper.getId();
		this.name = paper.getName();
		this.academicField = paper.getAcademicField();
		this.author = paper.getAuthor();
	}

}
