package com.minghua.microservices.order.service;

import com.minghua.microservices.order.client.InventoryClient;
import com.minghua.microservices.order.dto.OrderRequest;
import com.minghua.microservices.order.exception.InventoryServiceUnavailableException;
import com.minghua.microservices.order.exception.ProductOutOfStockException;
import com.minghua.microservices.order.model.Order;
import com.minghua.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock == 1) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setSkuCode(orderRequest.skuCode());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
        } else if (isProductInStock == 0) {
            throw new ProductOutOfStockException("Product with SkuCode " + orderRequest.skuCode() + " is not enough in stock");
        } else {
            throw new InventoryServiceUnavailableException("Inventory service is currently unavailable. Please try again later.");
        }
    }
}
