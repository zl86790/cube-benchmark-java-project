package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.GiftCard;
import com.ecshop.service.GiftCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/gift-cards")
@RequiredArgsConstructor
public class GiftCardController {
    private final GiftCardService giftCardService;

    @PostMapping
    public ApiResponse<GiftCard> issue(
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) LocalDateTime expiresAt) {
        return ApiResponse.success(giftCardService.issueGiftCard(amount, expiresAt));
    }

    @GetMapping("/{code}")
    public ApiResponse<GiftCard> getGiftCard(@PathVariable String code) {
        return ApiResponse.success(giftCardService.getGiftCard(code));
    }

    @PostMapping("/{code}/redeem")
    public ApiResponse<GiftCard> redeem(@PathVariable String code, @RequestParam BigDecimal amount) {
        return ApiResponse.success(giftCardService.redeem(code, amount, "customer request"));
    }
}
