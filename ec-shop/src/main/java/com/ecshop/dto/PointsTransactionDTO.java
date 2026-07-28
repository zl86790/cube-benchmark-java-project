package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransactionDTO {
    private Long id;
    private String type;
    private Long points;
    private String description;
    private LocalDateTime createdAt;
}
