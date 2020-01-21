package rs.ac.uns.ftn.upp.upp.service.entityservice.journal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.order.Order;
import rs.ac.uns.ftn.upp.upp.model.order.OrderStatus;
import rs.ac.uns.ftn.upp.upp.repository.OrderRepository;


@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	public Order saveOrder(Order order) {
		return orderRepository.save(order);
	}

	public Order findById(Integer id) throws NotFoundException {
		Optional<Order> order = orderRepository.findById(id);
		
		if(!order.isPresent()) {
			throw new NotFoundException(id, Order.class.getSimpleName());
		}
		
		return order.get();
	}
	
	public List<Order> findAll() {
		return orderRepository.findAllByStatus(OrderStatus.COMPLETED);
	}
}
