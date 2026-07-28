package com.ecshop.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String code) {
        super("Insufficient gift card balance for code: " + code);
    }
}
