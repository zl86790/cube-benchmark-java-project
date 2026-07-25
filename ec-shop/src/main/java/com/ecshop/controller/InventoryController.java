package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Inventory;
import com.ecshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ApiResponse<Inventory> getInventory(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getInventory(productId);
        return ApiResponse.success(inventory);
    }

    @PutMapping("/product/{productId}")
    public ApiResponse<Inventory> updateInventory(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Integer reorderThreshold) {
        Inventory inventory = inventoryService.updateInventory(productId, quantity, reorderThreshold);
        return ApiResponse.success(inventory);
    }
}
