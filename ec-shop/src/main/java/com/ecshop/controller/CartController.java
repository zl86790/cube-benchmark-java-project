package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.CartDTO;
import com.ecshop.model.Cart;
import com.ecshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ApiResponse<CartDTO> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ApiResponse.success(cartService.toDTO(cart));
    }

    @PostMapping("/{userId}/items")
    public ApiResponse<CartDTO> addItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.addItem(userId, productId, quantity);
        return ApiResponse.success(cartService.toDTO(cart));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ApiResponse<CartDTO> removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        Cart cart = cartService.removeItem(userId, itemId);
        return ApiResponse.success(cartService.toDTO(cart));
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ApiResponse<CartDTO> updateItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateItemQuantity(userId, itemId, quantity);
        return ApiResponse.success(cartService.toDTO(cart));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.success("Cart cleared", null);
    }
}
