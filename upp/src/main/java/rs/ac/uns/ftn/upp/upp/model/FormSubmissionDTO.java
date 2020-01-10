package rs.ac.uns.ftn.upp.upp.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Lambok annotations
@Getter
@Setter
@NoArgsConstructor
public class FormSubmissionDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7569636164115917482L;
	private String fieldId;
	private String fieldValue;

	public FormSubmissionDTO(String fieldId, String fieldValue) {
		super();
		this.fieldId = fieldId;
		this.fieldValue = fieldValue;
	}


}
