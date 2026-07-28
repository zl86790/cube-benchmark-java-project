package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAnswerDTO {

    private Long id;
    private Long userId;
    private String answer;
    private LocalDateTime createdAt;
}
