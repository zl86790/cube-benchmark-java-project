package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.CouponDTO;
import com.ecshop.model.Coupon;
import com.ecshop.model.CouponRedemption;
import com.ecshop.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ApiResponse<CouponDTO> createCoupon(@RequestBody Coupon coupon) {
        Coupon created = couponService.createCoupon(coupon);
        return ApiResponse.success(couponService.toDTO(created));
    }

    @GetMapping("/{code}")
    public ApiResponse<CouponDTO> getCoupon(@PathVariable String code) {
        Coupon coupon = couponService.getCoupon(code);
        return ApiResponse.success(couponService.toDTO(coupon));
    }

    @PostMapping("/{code}/redeem")
    public ApiResponse<BigDecimal> redeemCoupon(
            @PathVariable String code,
            @RequestParam Long userId,
            @RequestParam Long orderId,
            @RequestParam BigDecimal orderAmount) {
        CouponRedemption redemption = couponService.redeem(code, userId, orderId, orderAmount);
        return ApiResponse.success(redemption.getDiscountAmount());
    }
}
