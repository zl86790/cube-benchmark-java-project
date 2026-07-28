package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.exception.PromotionExpiredException;
import com.ecshop.model.FlashSaleItem;
import com.ecshop.model.Product;
import com.ecshop.model.Promotion;
import com.ecshop.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductService productService;

    public Promotion getPromotion(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Promotion not found: " + id));
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }

    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion addFlashSaleItem(Long promotionId, Long productId, BigDecimal salePrice, Integer stockLimit) {
        Promotion promotion = getPromotion(promotionId);
        Product product = productService.getProduct(productId);

        FlashSaleItem item = new FlashSaleItem();
        item.setPromotion(promotion);
        item.setProduct(product);
        item.setSalePrice(salePrice);
        item.setStockLimit(stockLimit);
        promotion.getItems().add(item);

        return promotionRepository.save(promotion);
    }

    @Transactional
    public FlashSaleItem purchaseFlashSaleItem(Long promotionId, Long flashSaleItemId, Integer quantity) {
        Promotion promotion = getPromotion(promotionId);
        LocalDateTime now = LocalDateTime.now();
        if (!promotion.getIsActive() || now.isBefore(promotion.getStartsAt()) || now.isAfter(promotion.getEndsAt())) {
            throw new PromotionExpiredException(promotionId);
        }

        FlashSaleItem item = promotion.getItems().stream()
                .filter(i -> i.getId().equals(flashSaleItemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Flash sale item not found: " + flashSaleItemId));

        if (item.getStockLimit() != null && item.getSoldCount() + quantity > item.getStockLimit()) {
            throw new BusinessException("Flash sale item is sold out");
        }

        item.setSoldCount(item.getSoldCount() + quantity);
        promotionRepository.save(promotion);
        return item;
    }
}
