package rs.ac.uns.ftn.upp.upp.service.emailservice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.model.user.Customer;
import rs.ac.uns.ftn.upp.upp.model.user.security.VerificationToken;
import rs.ac.uns.ftn.upp.upp.repository.user.security.VerificationTokenRepository;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    public void createVerificationToken(Customer customer, String token) {
    	System.err.println("usao u create verification token: " + token);
        tokenRepository.save(new VerificationToken(token, customer));
    }
    
    public Optional<VerificationToken> findByToken(String token) {
    	System.err.println("usao  find by token: " + token);
    	Optional<VerificationToken> temp = tokenRepository.findByToken(token);
    	System.err.println("find by token + " +  temp.get().getToken());
        return tokenRepository.findByToken(token);
    }
}