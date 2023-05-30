package com.ms.orderservice.service;

import com.ms.orderservice.dto.InventoryResponse;
import com.ms.orderservice.dto.OrderItemRequest;
import com.ms.orderservice.dto.OrderRequest;
import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderItem;
import com.ms.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItem> orderItems = orderRequest.getOrderItemsRequest()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderItems(orderItems);

        List<String> skuCodes = order.getOrderItems()
                .stream()
                .map(OrderItem::getSkuCode)
                .toList();

        Function<UriBuilder, URI> buildUriParams = uriBuilder -> uriBuilder.queryParam("sku-code", skuCodes).build();

        InventoryResponse[] inventoryResponses = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventory", buildUriParams)
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean productsInStock = Arrays.stream(Objects.requireNonNull(inventoryResponses))
                .allMatch(InventoryResponse::getIsInStock);

        if(productsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later.");
        }
    }

    private OrderItem mapToDto(OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(orderItemRequest.getPrice());
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setSkuCode(orderItemRequest.getSkuCode());
        return orderItem;
    }
}
