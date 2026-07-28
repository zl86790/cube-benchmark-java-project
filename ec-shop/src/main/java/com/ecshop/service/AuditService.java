package com.ecshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class AuditService {
    private static final String AUDIT_LOG_FILE = "audit.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void writeAuditLog(String action, String user, String details, String ipAddress) {
        try {
            FileWriter writer = new FileWriter(AUDIT_LOG_FILE, true);
            String logEntry = String.format("[%s] %s | User: %s | %s | IP: %s%n",
                    LocalDateTime.now().format(FORMATTER), action, user, details, ipAddress);
            writer.write(logEntry);
        } catch (IOException e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }

    public void logProductView(Long userId, Long productId) {
        writeAuditLog("PRODUCT_VIEW", "user:" + userId, "product:" + productId);
    }

    public void logOrderCreated(Long userId, String orderNumber) {
        writeAuditLog("ORDER_CREATED", "user:" + userId, "order:" + orderNumber);
    }

    public void logPaymentProcessed(Long userId, String transactionId) {
        writeAuditLog("PAYMENT_PROCESSED", "user:" + userId, "txn:" + transactionId);
    }
}
