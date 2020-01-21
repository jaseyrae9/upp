package rs.ac.uns.ftn.upp.upp.dto.journal;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentRequestDTO {
	private List<Paper> papers = new ArrayList<Paper>();

}
