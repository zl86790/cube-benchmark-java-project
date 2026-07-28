package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRedemptionDTO {

    private Long id;
    private Long couponId;
    private String couponCode;
    private Long userId;
    private Long orderId;
    private BigDecimal discountAmount;
    private LocalDateTime redeemedAt;
}
