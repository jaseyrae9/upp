package rs.ac.uns.ftn.upp.upp.dto.journal;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderResponseDTO {
	private UUID id;
	private Integer orderIdScienceCenter;
	private Double price;
	private String callbackUrl;
}
