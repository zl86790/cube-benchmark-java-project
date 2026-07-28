package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Warehouse;
import com.ecshop.model.WarehouseStock;
import com.ecshop.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping
    public ApiResponse<List<Warehouse>> getActiveWarehouses() {
        return ApiResponse.success(warehouseService.getActiveWarehouses());
    }

    @PostMapping
    public ApiResponse<Warehouse> createWarehouse(@RequestBody Warehouse warehouse) {
        return ApiResponse.success(warehouseService.createWarehouse(warehouse));
    }

    @GetMapping("/{id}/stock")
    public ApiResponse<List<WarehouseStock>> getStock(@PathVariable Long id) {
        return ApiResponse.success(warehouseService.getStockByWarehouse(id));
    }

    @PutMapping("/{id}/stock/{productId}")
    public ApiResponse<WarehouseStock> setStock(
            @PathVariable Long id,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ApiResponse.success(warehouseService.setStock(id, productId, quantity));
    }
}
