package rs.ac.uns.ftn.upp.upp.controller.journal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import rs.ac.uns.ftn.upp.upp.dto.journal.OrderResponseDTO;
import rs.ac.uns.ftn.upp.upp.dto.journal.PaymentRequestDTO;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.order.Order;
import rs.ac.uns.ftn.upp.upp.model.order.OrderStatus;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.OrderService;

@RestController
@RequestMapping("/paper")
public class PaperController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value="/pay", method = RequestMethod.POST, consumes = "application/json")
	public String  makePayment(@RequestBody PaymentRequestDTO paymentRequestDTO,  HttpServletResponse httpServletResponse) {
		System.out.println("usao u pay controller");
		// Pretpostavka: Svi artikli bi trebalo da pripadaju jednom prodavcu
		String merchantApiKey = "";
		System.err.println("request: " + paymentRequestDTO );
		
		for(Paper paper: paymentRequestDTO.getPapers()) {
			System.err.println("autor username " + paper.getAuthor().getUsername());
			System.err.println(paper.getAuthor());
			merchantApiKey = paper.getAuthor().getApi_key();
			System.err.println("merchantApiKey " + merchantApiKey);

			break;
		}
		System.err.println("merchantApiKey " + merchantApiKey);

		double price = 0;
		
		for(Paper paper: paymentRequestDTO.getPapers()) {
			System.out.println("cena " + paper.getPrice());
			price += paper.getPrice();
		}
		System.err.println("konacna cena " + price);
		
		Order o = new Order();
		o.setPrice(price);
		// TODO: NAPAVI ENDPOINT
		o.setCallbackUrl("http://localhost:8080/order/complete");
		o.setStatus(OrderStatus.CREATED);
		o.setPapers(paymentRequestDTO.getPapers());
		Order savedOrder = orderService.saveOrder(o);
		
		System.err.println(savedOrder);
		
		String paymentConcentratorUrl = "http://localhost:8762/payment-concentrator";
		System.out.println(paymentConcentratorUrl + "/order/create");
		ResponseEntity<OrderResponseDTO> responseEntity = restTemplate.exchange(paymentConcentratorUrl + "/order/create", HttpMethod.POST,
				new HttpEntity<Order>(savedOrder), OrderResponseDTO.class);
		System.out.println(responseEntity.getBody());
		return paymentConcentratorUrl + "/choosePaymentMethod/" + merchantApiKey + "/" + responseEntity.getBody().getId();
	
	}

}
