package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardDTO {

    private Long id;
    private String code;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Boolean isActive;
    private LocalDateTime expiresAt;
}
