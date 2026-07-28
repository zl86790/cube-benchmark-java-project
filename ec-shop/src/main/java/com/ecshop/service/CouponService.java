package com.ecshop.service;

import com.ecshop.dto.CouponDTO;
import com.ecshop.exception.CouponExpiredException;
import com.ecshop.exception.CouponNotFoundException;
import com.ecshop.model.Coupon;
import com.ecshop.model.CouponRedemption;
import com.ecshop.model.User;
import com.ecshop.repository.CouponRedemptionRepository;
import com.ecshop.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists: " + coupon.getCode());
        }
        return couponRepository.save(coupon);
    }

    public Coupon getCoupon(String code) {
        return couponRepository.findByCode(code);
    }

    public void validateCoupon(Coupon coupon, BigDecimal orderAmount) {
        if (!coupon.getIsActive()) {
            throw new CouponExpiredException(coupon.getCode());
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new CouponExpiredException(coupon.getCode());
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new CouponExpiredException(coupon.getCode());
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new CouponExpiredException(coupon.getCode());
        }
        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new IllegalArgumentException("Order amount does not meet the coupon minimum");
        }
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getDiscountValue();
        }
        if (coupon.getMaxDiscountAmount() != null) {
            discount = discount.min(coupon.getMaxDiscountAmount());
        }
        return discount.min(orderAmount);
    }

    @Transactional
    public CouponRedemption redeem(String code, Long userId, Long orderId, BigDecimal orderAmount) {
        Coupon coupon = getCoupon(code);
        validateCoupon(coupon, orderAmount);

        BigDecimal discount = calculateDiscount(coupon, orderAmount);

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        CouponRedemption redemption = new CouponRedemption();
        redemption.setCoupon(coupon);
        User user = new User();
        user.setId(userId);
        redemption.setUser(user);
        redemption.setOrderId(orderId);
        redemption.setDiscountAmount(discount);

        return couponRedemptionRepository.save(redemption);
    }

    public List<CouponRedemption> getRedemptionsByUser(Long userId) {
        return couponRedemptionRepository.findByUserId(userId);
    }

    public CouponDTO toDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDescription(coupon.getDescription());
        dto.setDiscountType(coupon.getDiscountType().name());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinOrderAmount(coupon.getMinOrderAmount());
        dto.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
        dto.setUsageLimit(coupon.getUsageLimit());
        dto.setUsageCount(coupon.getUsageCount());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidUntil(coupon.getValidUntil());
        dto.setIsActive(coupon.getIsActive());
        return dto;
    }

    public List<CouponDTO> toDTOList(List<Coupon> coupons) {
        return coupons.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
