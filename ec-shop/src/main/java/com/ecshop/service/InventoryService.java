package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.exception.InsufficientInventoryException;
import com.ecshop.model.Inventory;
import com.ecshop.model.OrderItem;
import com.ecshop.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // BUG #1 (HIGH): Race condition - no synchronization or locking
    // When multiple concurrent orders try to deduct the same product's stock,
    // they can both read the same availableQuantity, both pass the check,
    // and both deduct, leading to overselling (negative inventory).
    @Transactional
    public boolean deductStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException("Inventory not found for product: " + item.getProduct().getId()));

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient stock for product " + item.getProduct().getId() +
                                ". Available: " + inventory.getAvailableQuantity() +
                                ", Requested: " + item.getQuantity());
            }

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.getQuantity());
            inventoryRepository.save(inventory);
        }
        return true;
    }

    @Transactional
    public void restoreStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException("Inventory not found for product: " + productId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventoryRepository.save(inventory);
        log.info("Restored {} to product {}. Current: {}",
                quantity, productId, inventory.getAvailableQuantity());
    }

    // COMPILE ERROR #2: Return type is String but method body returns Inventory
    public String getInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException("Inventory not found for product: " + productId));
    }

    // COMPILE ERROR #2b: updateInventory calls getInventory which now returns String,
    // so assigning String to Inventory and calling inventory.setAvailableQuantity()
    // will both fail (String doesn't have those methods).
    @Transactional
    public Inventory updateInventory(Long productId, Integer quantity, Integer reorderThreshold) {
        Inventory inventory = getInventory(productId);
        inventory.setAvailableQuantity(quantity);
        inventory.setReorderThreshold(reorderThreshold);
        return inventoryRepository.save(inventory);
    }
}
