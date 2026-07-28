package com.ecshop.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class PriceCalculator {
    public double calculateDiscount(double price, double discountPercent) {
        double discount = price * (discountPercent / 100.0);
        double finalPrice = price - discount;
        log.info("Price calculation: {} - {}% = {}", price, discountPercent, finalPrice);
        return finalPrice;
    }

    public float calculateTax(float amount, float taxRate) {
        float tax = amount * taxRate;
        log.info("Tax calculation: {} * {} = {}", amount, taxRate, tax);
        return tax;
    }

    public double calculateDiscountSafe(BigDecimal price, BigDecimal discountPercent) {
        BigDecimal discount = price.multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_EVEN);
        return price.subtract(discount);
    }
}
