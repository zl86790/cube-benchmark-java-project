package com.ecshop.service;

import com.ecshop.dto.CartDTO;
import com.ecshop.dto.CartItemDTO;
import com.ecshop.model.Cart;
import com.ecshop.model.CartItem;
import com.ecshop.model.Product;
import com.ecshop.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// COMPILE ERROR #4: Missing import for BigDecimal - used extensively in toDTO()
// import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart createNewCart(Long userId) {
        // Simplified: in real app, would fetch user
        Cart cart = new Cart();
        // cart.setUser(...);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addItem(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productService.getProduct(productId);

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartDTO toDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setTotalItems(cart.getItems().size());

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(total);

        dto.setItems(cart.getItems().stream().map(item -> {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return itemDTO;
        }).toList());

        return dto;
    }
}
