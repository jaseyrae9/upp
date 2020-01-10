package rs.ac.uns.ftn.upp.upp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
