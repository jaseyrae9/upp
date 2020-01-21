package rs.ac.uns.ftn.upp.upp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.upp.upp.model.order.Order;
import rs.ac.uns.ftn.upp.upp.model.order.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Integer>{
	List<Order> findAllByStatus(OrderStatus status);
}
