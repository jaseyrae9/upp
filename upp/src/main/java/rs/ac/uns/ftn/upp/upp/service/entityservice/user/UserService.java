package rs.ac.uns.ftn.upp.upp.service.entityservice.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.MyUser;
import rs.ac.uns.ftn.upp.upp.repository.user.MyUserRepository;


@Service
public class UserService {

	@Autowired
	private MyUserRepository userRepository;
	
	/**
	 * Saves user to datebase. Id is autogenerated.
	 * 
	 * @param user
	 * @return saved user with id set
	 */
	public MyUser saveUser(MyUser user) {
		return userRepository.save(user);
	}
	
	public Iterable<MyUser> findAll() {
		return userRepository.findAll();
	}

}