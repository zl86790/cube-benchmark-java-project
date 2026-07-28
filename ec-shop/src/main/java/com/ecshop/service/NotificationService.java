package com.ecshop.service;

import com.ecshop.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendOrderConfirmation(Order order) {
        log.info("Sending order confirmation email to {} for order {}",
                order.getUser().getEmail(), order.getOrderNumber());
    }

    public void sendOrderShippedNotification(Order order) {
        log.info("Sending shipping notification to {} for order {}",
                order.getUser().getEmail(), order.getOrderNumber());
    }

    public void sendOrderCancelledNotification(Order order) {
        log.info("Sending cancellation notification to {} for order {}",
                order.getUser().getEmail(), order.getOrderNumber());
    }

    public void sendPaymentFailedNotification(Order order) {
        log.info("Sending payment failed notification to {} for order {}",
                order.getUser().getEmail(), order.getOrderNumber());
    }
}
