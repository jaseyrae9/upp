package rs.ac.uns.ftn.upp.upp.dto.journal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CompletePaymentDTO {
	private Integer order_id;
	private String status;
}
