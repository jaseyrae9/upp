package rs.ac.uns.ftn.upp.upp.controller.journal;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import rs.ac.uns.ftn.upp.upp.dto.journal.OrderResponseDTO;
import rs.ac.uns.ftn.upp.upp.dto.journal.PaymentRequestDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.order.Order;
import rs.ac.uns.ftn.upp.upp.model.order.OrderStatus;
import rs.ac.uns.ftn.upp.upp.model.user.Buyer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.OrderService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.BuyerService;

@RestController
@RequestMapping("/paper")
public class PaperController {
	
	@Autowired
	private OrderService orderService;

	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private BuyerService buyerService;

	@PreAuthorize("hasAnyRole('CUSTOMER')")
	@RequestMapping(value="/pay", method = RequestMethod.POST, consumes = "application/json")
	public String  makePayment(@RequestBody PaymentRequestDTO paymentRequestDTO,  Authentication authentication, HttpServletResponse httpServletResponse) throws NotFoundException {
		System.out.println("usao u pay controller");
		String username = authentication.getName();
		System.out.println("username: " + username);
		Optional<Buyer> buyerOpt = buyerService.findBuyer(username);
		if(!buyerOpt.isPresent()) {
			throw new NotFoundException(username, Buyer.class.getSimpleName());
		}
		Buyer buyer = buyerOpt.get(); 
		
		// Pretpostavka: Svi artikli bi trebalo da pripadaju jednom prodavcu
		String merchantApiKey = "";
		
		for(Journal journal: paymentRequestDTO.getJournals()) {
			System.err.println("editor username " + journal.getEditorInChief().getUsername());
			System.err.println(journal.getEditorInChief());
			merchantApiKey = journal.getEditorInChief().getApi_key();
			System.err.println("merchantApiKey " + merchantApiKey);
			break;
		}
		System.err.println("merchantApiKey " + merchantApiKey);

		double price = 0;
		
		for(Journal journal: paymentRequestDTO.getJournals()) {
			System.out.println("cena " + journal.getPrice());
			price += journal.getPrice();
		}
		System.err.println("konacna cena " + price);
		
		Order o = new Order();
		o.setPrice(price);
		o.setCallbackUrl("http://localhost:8080/order/complete");
		o.setStatus(OrderStatus.CREATED);
		o.setJournals(paymentRequestDTO.getJournals());
		o.setBuyer(buyer);
		Order savedOrder = orderService.saveOrder(o);
		
		// System.err.println(savedOrder);
		
		String paymentConcentratorUrl = "http://localhost:8762/payment-concentrator";
		System.out.println(paymentConcentratorUrl + "/order/create");
		ResponseEntity<OrderResponseDTO> responseEntity = restTemplate.exchange(paymentConcentratorUrl + "/order/create", HttpMethod.POST,
				new HttpEntity<Order>(savedOrder), OrderResponseDTO.class);
		System.out.println(responseEntity.getBody());
		return paymentConcentratorUrl + "/choosePaymentMethod/" + merchantApiKey + "/" + responseEntity.getBody().getId();
	
	}


	
	
	
	
	
	
	
	
}
