package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTierDTO {
    private Long id;
    private String name;
    private BigDecimal minSpend;
    private BigDecimal discountPercentage;
    private String description;
}
