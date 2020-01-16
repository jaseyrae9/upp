package rs.ac.uns.ftn.upp.upp.dto;

import java.util.List;

import org.camunda.bpm.engine.form.FormField;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Lambok annotations
@Getter
@Setter
@NoArgsConstructor
public class FormFieldsDTO {
	String taskId;
	List<FormField> formFields;
	String processInstanceId;

	public FormFieldsDTO(String taskId, String processInstanceId, List<FormField> formFields) {
		super();
		this.taskId = taskId;
		this.formFields = formFields;
		this.processInstanceId = processInstanceId;
	}
	
}
