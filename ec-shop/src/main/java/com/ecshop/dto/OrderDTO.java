package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private Long userId;
    private String status;
    private List<OrderItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
}
