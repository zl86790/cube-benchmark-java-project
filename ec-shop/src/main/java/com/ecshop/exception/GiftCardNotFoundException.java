package com.ecshop.exception;

public class GiftCardNotFoundException extends RuntimeException {

    public GiftCardNotFoundException(String code) {
        super("Gift card not found with code: " + code);
    }
}
