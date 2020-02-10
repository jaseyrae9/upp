package rs.ac.uns.ftn.upp.upp.service.entityservice.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.camunda.bpm.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.dto.user.UserDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.exceptions.RequestDataException;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.Authority;
import rs.ac.uns.ftn.upp.upp.repository.user.CustomerRepository;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.security.AuthorityService;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AuthorityService authorityService;

	@Autowired
	private IdentityService identityService;
	
	public Customer saveCustomer(Customer customer) {
		System.err.println("cuvamo customera");

		return customerRepository.save(customer);
	}

	public Optional<Customer> findCustomer(String username) {
		return customerRepository.findByUsername(username);
	}

	public Iterable<Customer> findAll() {
		return customerRepository.findAll();
	}

	public Customer findById(Integer id) throws NotFoundException {
		Optional<Customer> userOpt = customerRepository.findById(id);
		if (!userOpt.isPresent()) {
			throw new NotFoundException(id, Customer.class.getSimpleName());
		}
		Customer user = userOpt.get();
		return user;
	}

	public Customer findByUsername(String name) {
		Customer ret = null;
		Optional<Customer> customer = customerRepository.findByUsername(name);
		if (customer.isPresent()) {
			ret = customer.get();
		}
		return ret;
	}

	public Set<UserDTO> getUsers() {
		System.err.println("Usao u get users servise");

		Iterable<Customer> users = customerRepository.findAllByActive(true);

		Authority adminAuthority = authorityService.findByName("ADMIN");
		Authority editorAuthority = authorityService.findByName("EDITOR");
		//Authority editorInChiefAuthority = authorityService.findByName("EDITORINCHIEF");

		List<Authority> authorities = new ArrayList<>();
		authorities.add(adminAuthority);
		authorities.add(editorAuthority);
		//authorities.add(editorInChiefAuthority);

		Set<UserDTO> ret = new HashSet<>();
		for (Customer user : users) {
			System.out.println("user " + user.getUsername() + " ima ovoliko uloga: " + user.getUserAuthorities().size());
			if (!user.getUserAuthorities().contains(editorAuthority) || !user.getUserAuthorities().contains(adminAuthority)) {
				ret.add(new UserDTO(user));
				System.out.println("user: " + user.getUsername());
			}

		}
		System.out.println("ret.size " + ret.size());
		for(UserDTO u: ret) {
			System.out.println("u " + u.getUsername());
		}

		return ret;
	}

	public Customer makeAdmin(Integer id) throws NotFoundException, RequestDataException {
		Customer ret = this.findById(id);
		Authority authority = authorityService.findByName("ADMIN");
		if(ret.getUserAuthorities().contains(authority)) {
			throw new RequestDataException("Korisnik " + ret.getUsername() + " vec ima ulogu admina.");
		}
		ret.getUserAuthorities().add(authority);
		customerRepository.save(ret);
		identityService.createMembership(ret.getUsername(), "camunda-admin");		
		return ret;
	}

	public Customer makeEditor(Integer id) throws NotFoundException, RequestDataException {
		Customer ret = this.findById(id);
		Authority authority = authorityService.findByName("EDITOR");
		if(ret.getUserAuthorities().contains(authority)) {
			throw new RequestDataException("Korisnik " + ret.getUsername() + " vec ima ulogu urednika.");
		}
		ret.getUserAuthorities().add(authority);
		customerRepository.save(ret);
		identityService.createMembership(ret.getUsername(), "editors");		

		return ret;
	}
}
