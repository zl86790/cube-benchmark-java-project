package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportDTO {
    private long totalProducts;
    private long lowStockProducts;
    private long outOfStockProducts;
    private int totalAvailableUnits;
}
