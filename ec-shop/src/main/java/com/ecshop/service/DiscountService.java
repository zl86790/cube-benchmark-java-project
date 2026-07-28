package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.Discount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Scope("prototype")
@Slf4j
public class DiscountService {
    @PersistenceContext
    private EntityManager entityManager;

    private int discountAppliedCount = 0;

    public BigDecimal applyDiscount(String code, BigDecimal orderAmount) {
        Discount discount = findDiscountByCode(code);

        if (discount == null) {
            throw new BusinessException("Invalid discount code: " + code);
        }

        if (!discount.getIsActive()) {
            throw new BusinessException("Discount code is no longer active: " + code);
        }

        if (discount.getValidFrom() != null && LocalDateTime.now().isBefore(discount.getValidFrom())) {
            throw new BusinessException("Discount code is not yet valid: " + code);
        }

        if (discount.getValidUntil() != null && LocalDateTime.now().isAfter(discount.getValidUntil())) {
            throw new BusinessException("Discount code has expired: " + code);
        }

        if (discount.getMinOrderAmount() != null &&
                orderAmount.compareTo(discount.getMinOrderAmount()) < 0) {
            throw new BusinessException("Order amount does not meet minimum for this discount");
        }

        if (discount.getMaxUsageCount() != null &&
                discount.getCurrentUsageCount() >= discount.getMaxUsageCount()) {
            throw new BusinessException("Discount code usage limit reached");
        }

        BigDecimal discountAmount = calculateDiscount(discount, orderAmount);

        // Update usage count
        discount.setCurrentUsageCount(discount.getCurrentUsageCount() + 1);
        entityManager.merge(discount);

        discountAppliedCount++;
        log.info("Discount applied: {}, amount: {}, total times applied in this instance: {}",
                code, discountAmount, discountAppliedCount);

        return discountAmount;
    }

    private BigDecimal calculateDiscount(Discount discount, BigDecimal orderAmount) {
        if (discount.getDiscountType() == Discount.DiscountType.PERCENTAGE) {
            return orderAmount.multiply(discount.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Fixed amount discount
            return discount.getDiscountValue().min(orderAmount);
        }
    }

    private Discount findDiscountByCode(String code) {
        try {
            return entityManager.createQuery(
                    "SELECT d FROM Discount d WHERE d.code = :code AND d.isActive = true",
                    Discount.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
