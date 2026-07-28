package com.ecshop.exception;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String code) {
        super("Coupon has expired or is no longer valid: " + code);
    }
}
