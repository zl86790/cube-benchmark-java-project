package com.ecshop.service;

import com.ecshop.dto.WishlistDTO;
import com.ecshop.dto.WishlistItemDTO;
import com.ecshop.model.Product;
import com.ecshop.model.User;
import com.ecshop.model.Wishlist;
import com.ecshop.model.WishlistItem;
import com.ecshop.repository.WishlistItemRepository;
import com.ecshop.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductService productService;

    public Wishlist getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> createWishlist(userId));
    }

    private Wishlist createWishlist(Long userId) {
        Wishlist wishlist = new Wishlist();
        User user = new User();
        user.setId(userId);
        wishlist.setUser(user);
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist addItem(Long userId, Long productId) {
        Wishlist wishlist = getWishlistByUserId(userId);
        if (wishlistItemRepository.existsByWishlistAndProduct(wishlist.getId(), productId)) {
            return wishlist;
        }
        Product product = productService.getProduct(productId);
        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);
        wishlist.getItems().add(item);
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist removeItem(Long userId, Long productId) {
        Wishlist wishlist = getWishlistByUserId(userId);
        wishlist.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return wishlistRepository.save(wishlist);
    }

    public WishlistDTO toDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setUserId(wishlist.getUser().getId());
        dto.setItems(wishlist.getItems().stream().map(item -> {
            WishlistItemDTO itemDTO = new WishlistItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setProductPrice(item.getProduct().getPrice());
            itemDTO.setAddedAt(item.getAddedAt());
            return itemDTO;
        }).collect(Collectors.toList()));
        return dto;
    }
}
