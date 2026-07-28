package com.ecshop.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public ProductNotFoundException(String sku) {
        super("User not found with sku: " + sku);
    }
}
