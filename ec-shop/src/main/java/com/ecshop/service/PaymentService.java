package com.ecshop.service;

import com.ecshop.dto.PaymentDTO;
import com.ecshop.exception.BusinessException;
import com.ecshop.model.Order;
import com.ecshop.model.Payment;
import com.ecshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Order order, Payment.PaymentMethod method, String creditCardNumber) {
        Payment payment = new Payment();
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(method);
        payment.setCreditCardNumber(creditCardNumber);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        // BUG #18 (LOW): Logging sensitive data (full credit card number) at INFO level
        // Should mask the card number or log at DEBUG level only
        log.info("Processing payment for order {}, amount: {}, card: {}",
                order.getOrderNumber(), order.getTotalAmount(), creditCardNumber);

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentGateway(payment);

        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());

            // BUG #7 (HIGH): Updates order status in a SEPARATE transaction context
            // If this transaction commits but OrderService's transaction rolls back,
            // we have inconsistent state - payment completed but order not persisted
            order.setStatus(Order.OrderStatus.CONFIRMED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentGateway(Payment payment) {
        // Simulate 95% success rate
        return Math.random() < 0.95;
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment not found with id: " + paymentId));
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("Payment not found for order: " + orderId));
    }

    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = getPayment(paymentId);
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BusinessException("Can only refund completed payments");
        }
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    public PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setStatus(payment.getStatus().name());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}
