package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.WishlistDTO;
import com.ecshop.model.Wishlist;
import com.ecshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    public ApiResponse<WishlistDTO> getWishlist(@PathVariable Long userId) {
        Wishlist wishlist = wishlistService.getWishlistByUserId(userId);
        return ApiResponse.success(wishlistService.toDTO(wishlist));
    }

    @PostMapping("/{userId}/items")
    public ApiResponse<WishlistDTO> addItem(@PathVariable Long userId, @RequestParam Long productId) {
        Wishlist wishlist = wishlistService.addItem(userId, productId);
        return ApiResponse.success(wishlistService.toDTO(wishlist));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ApiResponse<WishlistDTO> removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        Wishlist wishlist = wishlistService.removeItem(userId, productId);
        return ApiResponse.success(wishlistService.toDTO(wishlist));
    }
}
