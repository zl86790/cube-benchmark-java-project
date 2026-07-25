package com.ecshop.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class PriceCalculator {

    // BUG #3 (HIGH): Uses double/float for financial calculations
    // Floating-point arithmetic suffers from precision errors, e.g.:
    // 0.1 + 0.2 != 0.3 in floating point
    // Monetary calculations MUST use BigDecimal exclusively
    public double calculateDiscount(double price, double discountPercent) {
        double discount = price * (discountPercent / 100.0);
        double finalPrice = price - discount;
        log.info("Price calculation: {} - {}% = {}", price, discountPercent, finalPrice);
        return finalPrice;
    }

    // BUG #3 continued: Even the "safe" method uses double internally
    public float calculateTax(float amount, float taxRate) {
        float tax = amount * taxRate;
        log.info("Tax calculation: {} * {} = {}", amount, taxRate, tax);
        return tax;
    }

    // This is the correct way using BigDecimal, but it's never called
    public BigDecimal calculateDiscountSafe(BigDecimal price, BigDecimal discountPercent) {
        BigDecimal discount = price.multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_EVEN);
        return price.subtract(discount);
    }
}
