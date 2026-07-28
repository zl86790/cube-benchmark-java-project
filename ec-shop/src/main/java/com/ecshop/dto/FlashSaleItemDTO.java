package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItemDTO {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal salePrice;
    private Integer stockLimit;
    private Integer soldCount;
}
