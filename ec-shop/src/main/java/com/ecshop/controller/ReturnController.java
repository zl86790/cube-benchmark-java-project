package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.RefundRecord;
import com.ecshop.model.ReturnRequest;
import com.ecshop.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ApiResponse<ReturnRequest> createReturnRequest(
            @RequestParam Long orderId,
            @RequestParam Long userId,
            @RequestParam String reason) {
        return ApiResponse.success(returnService.createReturnRequest(orderId, userId, reason));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<ReturnRequest> approve(@PathVariable Long id) {
        return ApiResponse.success(returnService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<ReturnRequest> reject(@PathVariable Long id) {
        return ApiResponse.success(returnService.reject(id));
    }

    @PostMapping("/{id}/refund")
    public ApiResponse<RefundRecord> refund(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return ApiResponse.success(returnService.processRefund(id, amount));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ReturnRequest>> getByUser(@PathVariable Long userId) {
        return ApiResponse.success(returnService.getReturnsByUser(userId));
    }
}
