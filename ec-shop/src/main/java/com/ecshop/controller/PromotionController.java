package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.FlashSaleItem;
import com.ecshop.model.Promotion;
import com.ecshop.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping
    public ApiResponse<List<Promotion>> getActivePromotions() {
        return ApiResponse.success(promotionService.getActivePromotions());
    }

    @PostMapping
    public ApiResponse<Promotion> createPromotion(@RequestBody Promotion promotion) {
        return ApiResponse.success(promotionService.createPromotion(promotion));
    }

    @PostMapping("/{id}/items")
    public ApiResponse<Promotion> addFlashSaleItem(
            @PathVariable Long id,
            @RequestParam Long productId,
            @RequestParam BigDecimal salePrice,
            @RequestParam(required = false) Integer stockLimit) {
        return ApiResponse.success(promotionService.addFlashSaleItem(id, productId, salePrice, stockLimit));
    }

    @PostMapping("/{id}/items/{itemId}/purchase")
    public ApiResponse<FlashSaleItem> purchase(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ApiResponse.success(promotionService.purchaseFlashSaleItem(id, itemId, quantity));
    }
}
