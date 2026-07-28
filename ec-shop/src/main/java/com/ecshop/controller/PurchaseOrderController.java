package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.PurchaseOrder;
import com.ecshop.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ApiResponse<PurchaseOrder> createPurchaseOrder(@RequestParam Long supplierId) {
        return ApiResponse.success(purchaseOrderService.createPurchaseOrder(supplierId));
    }

    @PostMapping("/{id}/items")
    public ApiResponse<PurchaseOrder> addItem(
            @PathVariable Long id,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal unitCost) {
        return ApiResponse.success(purchaseOrderService.addItem(id, productId, quantity, unitCost));
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<PurchaseOrder> submit(@PathVariable Long id) {
        return ApiResponse.success(purchaseOrderService.submit(id));
    }

    @PostMapping("/{id}/receive")
    public ApiResponse<PurchaseOrder> receive(@PathVariable Long id) {
        return ApiResponse.success(purchaseOrderService.receive(id));
    }

    @GetMapping("/supplier/{supplierId}")
    public ApiResponse<List<PurchaseOrder>> getBySupplier(@PathVariable Long supplierId) {
        return ApiResponse.success(purchaseOrderService.getBySupplier(supplierId));
    }
}
