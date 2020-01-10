package rs.ac.uns.ftn.upp.upp.controller;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.ac.uns.ftn.upp.upp.model.FormFieldsDTO;
import rs.ac.uns.ftn.upp.upp.model.TaskDTO;

@Controller
@RequestMapping("/register")
public class RegisterController {
	@SuppressWarnings("unused")
	@Autowired 
	private IdentityService identityService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@SuppressWarnings("unused")
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private FormService formService;
	
	/**
	 * Uzimamo prvi user task iz bpmn modela i polja forme tog user taska, zatim ta polja saljemo frontu.
	 * @return formfield dto za front
	 */
	// saljemo formu za user task na front
	@GetMapping(path = "/get", produces = "application/json")
    public @ResponseBody FormFieldsDTO get() {
		System.err.println("U kontroleru");
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("registracijaKorisnika");
		System.err.println("pii: " + pi.toString());
		// uzimamo prvi task
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);
		
		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(task.getId());
		
		// lista form fildova i ispise u konzoli
		List<FormField> properties = tfd.getFormFields();
		for(FormField fp : properties) {
			System.out.println(fp.getId() + fp.getType());
		}
		
		// pravi dto u koji smesti taj task id, process instance id i te propertis odnosno te form fieldove i to se salje na front
        return new FormFieldsDTO(task.getId(), pi.getId(), properties);
    }
	
	
	/**
	 * 
	 * @param processInstanceId
	 * @return
	 */
	@GetMapping(path = "/get/tasks/{processInstanceId}", produces = "application/json")
    public @ResponseBody ResponseEntity<List<TaskDTO>> get(@PathVariable String processInstanceId) {
		
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		List<TaskDTO> dtos = new ArrayList<TaskDTO>();
		for (Task task : tasks) {
			TaskDTO t = new TaskDTO(task.getId(), task.getName(), task.getAssignee());
			dtos.add(t);
		}
		
        return new ResponseEntity<List<TaskDTO>>(dtos,  HttpStatus.OK);
    }
	
	
}
