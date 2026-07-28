package com.ecshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class TaxService {
    @Value("${ecshop.tax.rate:0.08}")
    private double taxRate;

    public BigDecimal calculateTax(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxRateDecimal = BigDecimal.valueOf(taxRate);
        BigDecimal taxAmount = amount.multiply(taxRateDecimal);

        return taxAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
