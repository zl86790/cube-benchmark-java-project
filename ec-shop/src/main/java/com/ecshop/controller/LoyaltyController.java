package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.LoyaltyAccount;
import com.ecshop.model.PointsTransaction;
import com.ecshop.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    @GetMapping("/{userId}")
    public ApiResponse<LoyaltyAccount> getAccount(@PathVariable Long userId) {
        return ApiResponse.success(loyaltyService.getAccount(userId));
    }

    @PostMapping("/{userId}/earn")
    public ApiResponse<LoyaltyAccount> earnPoints(
            @PathVariable Long userId,
            @RequestParam Long points,
            @RequestParam(required = false) String description) {
        return ApiResponse.success(loyaltyService.earnPoints(userId, points, description));
    }

    @PostMapping("/{userId}/redeem")
    public ApiResponse<LoyaltyAccount> redeemPoints(
            @PathVariable Long userId,
            @RequestParam Long points,
            @RequestParam(required = false) String description) {
        return ApiResponse.success(loyaltyService.redeemPoints(userId, points, description));
    }

    @GetMapping("/{userId}/history")
    public ApiResponse<List<PointsTransaction>> getHistory(@PathVariable Long userId) {
        return ApiResponse.success(loyaltyService.getHistory(userId));
    }
}
