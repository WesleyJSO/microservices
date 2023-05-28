package com.ms.orderservice.service;

import com.ms.orderservice.dto.OrderItemRequest;
import com.ms.orderservice.dto.OrderRequest;
import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderItem;
import com.ms.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

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
