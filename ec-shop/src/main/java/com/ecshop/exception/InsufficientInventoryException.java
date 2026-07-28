package com.ecshop.exception;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(Long productId, int available, int requested) {
        super(String.format("Insufficient inventory for product %d: available=%d, requested=%d",
                productId, available, requested));
    }
}
