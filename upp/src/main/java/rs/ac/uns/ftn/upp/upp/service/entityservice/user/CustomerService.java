package rs.ac.uns.ftn.upp.upp.service.entityservice.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.AcademicField;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.MyUser;
import rs.ac.uns.ftn.upp.upp.repository.user.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
		
	public Customer saveCustomer(Customer customer) {
		System.err.println("cuvamo customera");

		return customerRepository.save(customer);
	}
	
	public Optional<Customer> findCustomer(String username){
		return customerRepository.findByUsername(username);
	}
	
	public Iterable<Customer> findAll() {
		return customerRepository.findAll();
	}
	
	public Customer findByUsername(String name) {
		Customer ret = null;
		Optional<Customer> customer = customerRepository.findByUsername(name);
		if(customer.isPresent()) {
			ret = customer.get();
		}
		return ret;
	}
}
