package rs.ac.uns.ftn.upp.upp.controller.journal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.upp.upp.dto.journal.CompletePaymentDTO;
import rs.ac.uns.ftn.upp.upp.dto.journal.JournalDTO;
import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.model.order.Order;
import rs.ac.uns.ftn.upp.upp.model.order.OrderStatus;
import rs.ac.uns.ftn.upp.upp.model.user.Buyer;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.OrderService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.user.BuyerService;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BuyerService buyerService;
	
	@RequestMapping(value = "/complete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String complete(@RequestBody CompletePaymentDTO completePaymentDTO) throws NotFoundException {
		System.out.println("Complete payment");
		System.out.println(completePaymentDTO);
		if(completePaymentDTO.getStatus().contentEquals("COMPLETED")) {
			Order o = orderService.findById(completePaymentDTO.getOrder_id());
			o.setStatus(OrderStatus.COMPLETED);	
			orderService.saveOrder(o);
			System.err.println("o.getOrderJournals().size() " + o.getOrderJournals().size());

			
			Buyer buyer = o.getBuyer();	
			
			for(Journal journal: o.getOrderJournals()) {
				// System.out.println(journal);
				buyer.getPurchasedJournals().add(journal);
			}
			System.err.println("dodati casopisi, sad ih ima " + buyer.getPurchasedJournals().size());
			buyerService.saveBuyer(buyer);
			
			return "Success";
		}
		else if(completePaymentDTO.getStatus().contentEquals("FAILED")) {
			Order o = orderService.findById(completePaymentDTO.getOrder_id());
			o.setStatus(OrderStatus.FAILED);	
			orderService.saveOrder(o);
			return "Failed";
		}
		
		return "Error";
	}
	
//	@RequestMapping(method = RequestMethod.GET)
//	public List<Order> completedOrders() {
//		return orderService.findAll();
//	}
	
	@PreAuthorize("hasAnyRole('BUYER')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getOrders(Authentication authentication) throws NotFoundException {
		System.out.println("usao u get orders");
		String username = authentication.getName();
		System.out.println("username: " + username);
		Optional<Buyer> buyerOpt = buyerService.findBuyer(username);
		if(!buyerOpt.isPresent()) {
			throw new NotFoundException(username, Buyer.class.getSimpleName());
		}
		Buyer buyer = buyerOpt.get(); 		
		
		List<JournalDTO> ret = new ArrayList<>();
		for (Journal journal : buyer.getPurchasedJournals()) {
			ret.add(new JournalDTO(journal));
		}
		
		System.err.println("size: " + ret.size());
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}
}
