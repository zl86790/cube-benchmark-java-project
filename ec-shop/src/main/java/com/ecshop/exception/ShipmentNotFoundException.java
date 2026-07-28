package com.ecshop.exception;

public class ShipmentNotFoundException extends RuntimeException {
    public ShipmentNotFoundException(Long orderId) {
        super("Shipment not found for order: " + orderId);
    }
}
