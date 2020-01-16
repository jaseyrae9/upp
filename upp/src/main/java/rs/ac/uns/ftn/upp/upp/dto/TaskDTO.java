package rs.ac.uns.ftn.upp.upp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Lambok annotations
@Getter
@Setter
@NoArgsConstructor
public class TaskDTO {
	
	String taskId;
	String name;
	String assignee;
	
	public TaskDTO(String taskId, String name, String assignee) {
		super();
		this.taskId = taskId;
		this.name = name;
		this.assignee = assignee;
	}

}
