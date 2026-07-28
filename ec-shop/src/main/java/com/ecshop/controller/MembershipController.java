package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.MembershipTier;
import com.ecshop.model.UserMembership;
import com.ecshop.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;

    @GetMapping("/tiers")
    public ApiResponse<List<MembershipTier>> getTiers() {
        return ApiResponse.success(membershipService.getTiers());
    }

    @PostMapping("/tiers")
    public ApiResponse<MembershipTier> createTier(@RequestBody MembershipTier tier) {
        return ApiResponse.success(membershipService.createTier(tier));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<UserMembership> getUserMembership(@PathVariable Long userId) {
        return ApiResponse.success(membershipService.getUserMembership(userId));
    }

    @PostMapping("/user/{userId}/evaluate")
    public ApiResponse<UserMembership> evaluateTier(@PathVariable Long userId, @RequestParam BigDecimal totalSpend) {
        return ApiResponse.success(membershipService.evaluateTier(userId, totalSpend));
    }
}
