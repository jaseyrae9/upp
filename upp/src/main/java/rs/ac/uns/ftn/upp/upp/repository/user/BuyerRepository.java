package rs.ac.uns.ftn.upp.upp.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.user.Buyer;

public interface BuyerRepository extends JpaRepository<Buyer, Integer> {
	Optional<Buyer> findByUsername(String username);

}
