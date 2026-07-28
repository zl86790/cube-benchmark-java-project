package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.ProductAnswer;
import com.ecshop.model.ProductQuestion;
import com.ecshop.service.ProductQAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-qa")
@RequiredArgsConstructor
public class ProductQAController {
    private final ProductQAService productQAService;

    @PostMapping("/questions")
    public ApiResponse<ProductQuestion> askQuestion(
            @RequestParam Long productId,
            @RequestParam Long userId,
            @RequestParam String question) {
        return ApiResponse.success(productQAService.askQuestion(productId, userId, question));
    }

    @PostMapping("/questions/{questionId}/answers")
    public ApiResponse<ProductAnswer> answerQuestion(
            @PathVariable Long questionId,
            @RequestParam Long userId,
            @RequestParam String answer) {
        return ApiResponse.success(productQAService.answerQuestion(questionId, userId, answer));
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductQuestion>> getQuestionsByProduct(@PathVariable Long productId) {
        return ApiResponse.success(productQAService.getQuestionsByProduct(productId));
    }
}
