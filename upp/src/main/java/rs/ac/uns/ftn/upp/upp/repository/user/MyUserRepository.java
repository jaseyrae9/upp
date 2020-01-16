package rs.ac.uns.ftn.upp.upp.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.user.MyUser;

public interface MyUserRepository extends JpaRepository<MyUser, Integer> {
	Optional<MyUser> findByUsername(String username);

}
