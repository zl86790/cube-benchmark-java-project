package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.PaymentDTO;
import com.ecshop.model.Payment;
import com.ecshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ApiResponse<PaymentDTO> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPayment(id);
        return ApiResponse.success(paymentService.toDTO(payment));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<PaymentDTO> getPaymentByOrder(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ApiResponse.success(paymentService.toDTO(payment));
    }

    @PostMapping("/{id}/refund")
    public ApiResponse<PaymentDTO> refundPayment(@PathVariable Long id) {
        Payment payment = paymentService.refundPayment(id);
        return ApiResponse.success(paymentService.toDTO(payment));
    }
}
