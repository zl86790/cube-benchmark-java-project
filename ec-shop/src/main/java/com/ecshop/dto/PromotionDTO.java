package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Long id;
    private String name;
    private String description;
    private List<FlashSaleItemDTO> items;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private Boolean isActive;
}
