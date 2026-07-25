package com.ecshop.exception;

// BUG #15 (LOW): Misleading error message - says "User not found"
// but this exception is for Product not found scenarios.
// The message is copy-pasted from UserNotFoundException template.
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        // BUG: Wrong message - says "User" instead of "Product"
        super("User not found with id: " + id);
    }

    public ProductNotFoundException(String sku) {
        // BUG: Also wrong - says "User" instead of "Product"
        super("User not found with sku: " + sku);
    }
}
