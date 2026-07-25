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

    // BUG #11 (MEDIUM): Uses HALF_UP rounding which can cause
    // cumulative rounding errors in tax calculation across multiple items.
    // For financial calculations, HALF_EVEN (banker's rounding) is standard
    // to minimize cumulative bias over many transactions.
    public BigDecimal calculateTax(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxRateDecimal = BigDecimal.valueOf(taxRate);
        BigDecimal taxAmount = amount.multiply(taxRateDecimal);

        // BUG: Using HALF_UP instead of HALF_EVEN (banker's rounding)
        // For financial systems, HALF_EVEN prevents cumulative rounding bias
        return taxAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
