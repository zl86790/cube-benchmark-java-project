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
public class PurchaseOrderDTO {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private String status;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;
}
