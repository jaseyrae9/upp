package rs.ac.uns.ftn.upp.upp.service.entityservice.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.Buyer;
import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.repository.user.BuyerRepository;

@Service
public class BuyerService {

	@Autowired
	private BuyerRepository buyerRepository;
		
	public Buyer saveBuyer(Buyer buyer) {
		System.err.println("cuvamo Buyer");

		return buyerRepository.save(buyer);
	}
	
	public Optional<Buyer> findBuyer(String username){
		return buyerRepository.findByUsername(username);
	}
	
	public Iterable<Buyer> findAll() {
		return buyerRepository.findAll();
	}
	
	public Buyer findByUsername(String name) {
		Buyer ret = null;
		Optional<Buyer> buyer = buyerRepository.findByUsername(name);
		if(buyer.isPresent()) {
			ret = buyer.get();
		}
		return ret;
	}
}
