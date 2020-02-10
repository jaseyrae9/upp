package rs.ac.uns.ftn.upp.upp;

import static org.camunda.bpm.engine.authorization.Authorization.*;
import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;

import javax.servlet.ServletContext;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.IdentityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import rs.ac.uns.ftn.upp.upp.service.entityservice.user.CustomerService;

public class DemoDataGenerator {
	private ProcessEngine engine;
	private IdentityServiceImpl identityService;

	public DemoDataGenerator(ProcessEngine processEngine) {
		this.engine = processEngine;
		this.identityService = (IdentityServiceImpl) engine.getIdentityService();
	}

	public void addUsers() {
		CustomerService customerService = new CustomerService();
		System.err.println("customerService; " + customerService);
		if (identityService.isReadOnly()) {
			return;
		}

		
		
		User singleResult = identityService.createUserQuery().userId("urednik").singleResult();
		
		if (singleResult != null) {
			return;
		}
		addEditorsGroup();
		addAuthorsGroup();
		addReviewersGroup();
		addCustomersGroup();

		//	private void addUser(String username, String firstname, String lastname, String password) {

		addUser("urednik", "Urednik", "Urednik", "demo");
		addUser("urednik1", "urednik1", "urednik1", "demo");
		addUser("urednik2", "urednik2", "urednik2", "demo");
		addUser("urednik3", "Urednik3", "Urednik3", "demo");
		addMembership("urednik", "editors");
		addMembership("urednik1", "editors");
		addMembership("urednik2", "editors");
		addMembership("urednik3", "editors");

		addUser("recenzent", "recenzent", "recenzent", "demo");
		addUser("recenzent1", "recenzent1", "recenzent1", "demo");
		addUser("recenzent2", "recenzent2", "recenzent2", "demo");
		addUser("recenzent3", "recenzent3", "recenzent3", "demo");
		addUser("recenzent4", "recenzent", "recenzent", "demo");
		addUser("recenzent5", "recenzent", "recenzent", "demo");
		addMembership("recenzent", "reviewers");
		addMembership("recenzent1", "reviewers");
		addMembership("recenzent2", "reviewers");
		addMembership("recenzent3", "reviewers");
		addMembership("recenzent4", "reviewers");
		addMembership("recenzent5", "reviewers");
		
		addUser("autor", "autor", "autor", "demo");
		addUser("autor1", "autor1", "autor1", "demo");
		addMembership("autor", "authors");
		addMembership("autor1", "authors");
		

			
	}
	private void addCustomersGroup() {
		String id = "customers";
		String name = "Customers";
		Group group = identityService.newGroup(id);
		group.setName(name);
		group.setType("WORKFLOW");
		identityService.saveGroup(group);
		
		
	}
	
	private void addEditorsGroup() {
		String id = "editors";
		String name = "Editors";
		Group group = identityService.newGroup(id);
		group.setName(name);
		group.setType("WORKFLOW");
		identityService.saveGroup(group);

		Authorization tasklistAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		// allow only tasklist
		tasklistAuth.setGroupId(id);
		tasklistAuth.addPermission(ACCESS);
		tasklistAuth.setResourceId("tasklist");
		tasklistAuth.setResource(APPLICATION);
		engine.getAuthorizationService().saveAuthorization(tasklistAuth);

		// da sme da kreira proces dodavanja casopisa
		Authorization processAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		processAuth.setGroupId(id);
		processAuth.addPermission(Permissions.CREATE_INSTANCE);
		processAuth.setResource(Resources.PROCESS_DEFINITION);
		processAuth.setResourceId("dodavanjeCasopisa");
		engine.getAuthorizationService().saveAuthorization(processAuth);
		
		// da sme da cita sve
		Authorization processAuth1 = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		processAuth1.setGroupId(id);
		processAuth1.addPermission(Permissions.READ);
		processAuth1.setResource(Resources.PROCESS_DEFINITION);
		processAuth1.setResourceId("*");
		engine.getAuthorizationService().saveAuthorization(processAuth1);		
		
		//zabrani
		Authorization revokeCreating = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revokeCreating.setGroupId(id);
		revokeCreating.removePermission(Permissions.CREATE_INSTANCE);
		revokeCreating.setResource(Resources.PROCESS_DEFINITION);
		revokeCreating.setResourceId("ObradaPodnetogTeksta");
		engine.getAuthorizationService().saveAuthorization(revokeCreating);
		
		Authorization revokeRegistration = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revokeRegistration.setGroupId(id);
		revokeRegistration.removePermission(Permissions.CREATE_INSTANCE);
		revokeRegistration.setResource(Resources.PROCESS_DEFINITION);
		revokeRegistration.setResourceId("registracijaKorisnika");
		engine.getAuthorizationService().saveAuthorization(revokeRegistration);
		
		
	
		// allow instance creation and task
		Authorization instanceAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		instanceAuth.setGroupId(id);
		instanceAuth.addPermission(Permissions.CREATE);
		instanceAuth.setResource(Resources.PROCESS_INSTANCE);
		instanceAuth.setResourceId("dodavanjeCasopisa");
		engine.getAuthorizationService().saveAuthorization(instanceAuth);
		
		Authorization revoke = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revoke.setGroupId(id);
		revoke.removePermission(Permissions.CREATE);
		revoke.setResource(Resources.PROCESS_INSTANCE);
		revoke.setResourceId("ObradaPodnetogTeksta");
		engine.getAuthorizationService().saveAuthorization(revoke);
		Authorization revoke2 = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revoke2.setGroupId(id);
		revoke2.removePermission(Permissions.CREATE);
		revoke2.setResource(Resources.PROCESS_INSTANCE);
		revoke2.setResourceId("registracijaKorisnika");
		engine.getAuthorizationService().saveAuthorization(revoke);

	}
	
	private void addAuthorsGroup() {
		String id = "authors";
		String name = "Authors";
		Group group = identityService.newGroup(id);
		group.setName(name);
		group.setType("WORKFLOW");
		identityService.saveGroup(group);

		Authorization tasklistAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		// allow only tasklist
		tasklistAuth.setGroupId(id);
		tasklistAuth.addPermission(ACCESS);
		tasklistAuth.setResourceId("tasklist");
		tasklistAuth.setResource(APPLICATION);
		engine.getAuthorizationService().saveAuthorization(tasklistAuth);

		// da sme da kreira proces obrade teksta
		Authorization processAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		processAuth.setGroupId(id);
		processAuth.addPermission(Permissions.CREATE_INSTANCE);
		processAuth.setResource(Resources.PROCESS_DEFINITION);
		processAuth.setResourceId("ObradaPodnetogTeksta");
		engine.getAuthorizationService().saveAuthorization(processAuth);
		
		// da sme da cita sve
		Authorization processAuth1 = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		processAuth1.setGroupId(id);
		processAuth1.addPermission(Permissions.READ);
		processAuth1.setResource(Resources.PROCESS_DEFINITION);
		processAuth1.setResourceId("*");
		engine.getAuthorizationService().saveAuthorization(processAuth1);
		
		
		//zabrani
		Authorization revokeCreating = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revokeCreating.setGroupId(id);
		revokeCreating.removePermission(Permissions.CREATE_INSTANCE);
		revokeCreating.setResource(Resources.PROCESS_DEFINITION);
		revokeCreating.setResourceId("dodavanjeCasopisa");
		engine.getAuthorizationService().saveAuthorization(revokeCreating);
		
		Authorization revokeRegistration = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revokeRegistration.setGroupId(id);
		revokeRegistration.removePermission(Permissions.CREATE_INSTANCE);
		revokeRegistration.setResource(Resources.PROCESS_DEFINITION);
		revokeRegistration.setResourceId("registracijaKorisnika");
		engine.getAuthorizationService().saveAuthorization(revokeRegistration);
		
	
		// allow instance creation and task
		Authorization instanceAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		instanceAuth.setGroupId(id);
		instanceAuth.addPermission(Permissions.CREATE);
		instanceAuth.setResource(Resources.PROCESS_INSTANCE);
		instanceAuth.setResourceId("*");
		engine.getAuthorizationService().saveAuthorization(instanceAuth);
		
		Authorization revoke = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revoke.setGroupId(id);
		revoke.removePermission(Permissions.CREATE);
		revoke.setResource(Resources.PROCESS_INSTANCE);
		revoke.setResourceId("dodavanjeCasopisa");
		engine.getAuthorizationService().saveAuthorization(revoke);
		Authorization revoke2 = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revoke2.setGroupId(id);
		revoke2.removePermission(Permissions.CREATE);
		revoke2.setResource(Resources.PROCESS_INSTANCE);
		revoke2.setResourceId("registracijaKorisnika");
		engine.getAuthorizationService().saveAuthorization(revoke);

	}
	
	private void addReviewersGroup() {
		String id = "reviewers";
		String name = "Reviewers";
		Group group = identityService.newGroup(id);
		group.setName(name);
		group.setType("WORKFLOW");
		identityService.saveGroup(group);

		Authorization tasklistAuth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		// allow only tasklist
		tasklistAuth.setGroupId(id);
		tasklistAuth.addPermission(ACCESS);
		tasklistAuth.setResourceId("tasklist");
		tasklistAuth.setResource(APPLICATION);
		engine.getAuthorizationService().saveAuthorization(tasklistAuth);

		// da sme da cita sve
		Authorization processAuth1 = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
		processAuth1.setGroupId(id);
		processAuth1.addPermission(Permissions.READ);
		processAuth1.setResource(Resources.PROCESS_DEFINITION);
		processAuth1.setResourceId("ObradaPodnetogTeksta");
		engine.getAuthorizationService().saveAuthorization(processAuth1);
		
		//zabrani
		Authorization revoke = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_REVOKE);
		revoke.setGroupId(id);
		revoke.removePermission(Permissions.CREATE_INSTANCE);
		revoke.setResource(Resources.PROCESS_DEFINITION);
		revoke.setResourceId("*");
		engine.getAuthorizationService().saveAuthorization(revoke);

	}
	
	private void addUser(String username, String firstname, String lastname, String password) {
		User user = identityService.newUser(username);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setPassword(password);
		identityService.saveUser(user);
	}

	private void addMembership(String username, String group) {
		identityService.createMembership(username, group);
	}

}
