package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuestionDTO {

    private Long id;
    private Long productId;
    private Long userId;
    private String question;
    private List<ProductAnswerDTO> answers;
    private LocalDateTime createdAt;
}
