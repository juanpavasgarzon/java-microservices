package com.pavas.order.service.service;

import com.pavas.order.service.dto.InventoryResponse;
import com.pavas.order.service.dto.OrderItemRequest;
import com.pavas.order.service.dto.OrderRequest;
import com.pavas.order.service.event.OrderPlacedEvent;
import com.pavas.order.service.model.Order;
import com.pavas.order.service.model.OrderItem;
import com.pavas.order.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderDate(orderRequest.getDate());

        List<OrderItem> items = orderRequest.getOrderItems()
                .stream()
                .map(this::mapToOrderItem)
                .toList();

        order.setItems(items);

        List<String> codes = order.getItems()
                .stream()
                .map(OrderItem::getCode)
                .toList();

        InventoryResponse[] inventoryResponse = webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/inventory", uri -> uri.queryParam("codes", codes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert inventoryResponse != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponse).allMatch(InventoryResponse::isInStock);

        if (!allProductsInStock) {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

        orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderPlacedEvent(this, order.getOrderNumber()));
    }

    private OrderItem mapToOrderItem(OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCode(orderItemRequest.getCode());
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setPrice(orderItemRequest.getPrice());
        return orderItem;
    }
}
