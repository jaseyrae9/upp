package rs.ac.uns.ftn.upp.upp.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.repository.AcademicFieldRepository;

@Service
public class AcademicFieldsHandler implements TaskListener {
	
	@Autowired
	private AcademicFieldRepository academicFieldRepository;
	
	@Autowired
	private FormService formService;

	// izvlacimo usere koje postoje u bazi
	public void notify(DelegateTask delegateTask) {
		System.err.println("Kreiran prvi task-popunjavamo naucne oblasti");

		// za taj task daj mi formu
		TaskFormData tfd = formService.getTaskFormData(delegateTask.getId());
		
		// lista form fildova i ispise u konzoli
		System.err.println("heeej: ");

		List<FormField> formFields = tfd.getFormFields();
		System.err.println("heeej: " + formFields.size());
		Map<String, String> items = new HashMap<>();
		Iterable<AcademicField> academicFields = academicFieldRepository.findAll();
		
		if(!formFields.isEmpty()) {
			System.err.println("prvi if");
			for(FormField field : formFields) {
				System.err.println("field " + field.getId());
				if(field.getId().equals("naucneOblasti")) {
					EnumFormType eft = (EnumFormType)field.getType();
					items = eft.getValues();
					System.err.println("drugi if");
					for(AcademicField academicField: academicFields) {
						items.put(academicField.getId().toString(), academicField.getName());
					}
				}
			}
		}	

	}
}