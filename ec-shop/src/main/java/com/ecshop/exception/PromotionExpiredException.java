package com.ecshop.exception;

public class PromotionExpiredException extends RuntimeException {
    public PromotionExpiredException(Long id) {
        super("Promotion is not currently active: " + id);
    }
}
