package com.ms.orderservice;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.AUTO;
import static jakarta.persistence.GenerationType.IDENTITY;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
class OrderController {

	private final OrderService orderService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String placeOrder(@RequestBody OrderRequest orderRequest) {
		orderService.placeOrder(orderRequest);
		return "Order place successfully";
	}
}

@Repository
interface OrderRepository extends JpaRepository<Order, Long> {}
@Service
@Transactional
@RequiredArgsConstructor
class OrderService {

	private final OrderRepository orderRepository;
	public void placeOrder(OrderRequest orderRequest) {

		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderItem> orderItems = orderRequest.getOrderItemsRequest()
				.stream()
				.map(this::mapToDto)
				.toList();

		order.setOrderItems(orderItems);

		orderRepository.save(order);
	}

	private OrderItem mapToDto(OrderItemRequest orderItemRequest) {
		OrderItem orderItem = new OrderItem();
		orderItem.setPrice(orderItemRequest.getPrice());
		orderItem.setQuantity(orderItemRequest.getQuantity());
		orderItem.setSkuCode(orderItemRequest.getSkuCode());
		return orderItem;
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class OrderRequest {
	private List<OrderItemRequest> orderItemsRequest;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class OrderItemRequest {
	private Long id;
	private String skuCode;
	private BigDecimal price;
	private Integer quantity;
}


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDER_ITEMS")
class OrderItem {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	private String skuCode;
	private BigDecimal price;
	private Integer quantity;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDERS")
class Order {
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	private String orderNumber;
	@OneToMany(cascade = ALL)
	private List<OrderItem> orderItems;

}