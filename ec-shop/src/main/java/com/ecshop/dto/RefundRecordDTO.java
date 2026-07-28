package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRecordDTO {
    private Long id;
    private Long returnRequestId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime processedAt;
}
