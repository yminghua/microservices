package com.minghua.microservices.inventory.service;

import com.minghua.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public int isInStock(String skuCode, Integer quantity) {
        boolean status = inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
        if (status) {
            return 1;
        } else {
            return 0;
        }
    }
}
