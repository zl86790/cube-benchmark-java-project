package com.ecshop.service;

import com.ecshop.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ShippingService {

    // BUG #19 (LOW): Hardcoded shipping fee instead of reading from configuration
    // This should be configurable via application.yml or database
    private static final double BASE_SHIPPING_FEE = 15.00;

    // BUG #19 continued: Free shipping threshold also hardcoded
    private static final double FREE_SHIPPING_THRESHOLD = 99.00;

    public BigDecimal calculateShippingFee(Order order) {
        if (order.getSubtotal() == null) {
            return BigDecimal.valueOf(BASE_SHIPPING_FEE);
        }

        // Free shipping over threshold
        if (order.getSubtotal().compareTo(BigDecimal.valueOf(FREE_SHIPPING_THRESHOLD)) >= 0) {
            log.info("Free shipping applied for order: {}", order.getOrderNumber());
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(BASE_SHIPPING_FEE);
    }
}
