package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.ReviewDTO;
import com.ecshop.dto.ReviewSummaryDTO;
import com.ecshop.model.Review;
import com.ecshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewDTO> createReview(
            @RequestParam Long productId,
            @RequestParam Long userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) List<String> imageUrls) {
        Review review = reviewService.createReview(productId, userId, rating, title, comment, imageUrls);
        return ApiResponse.success(reviewService.toDTO(review));
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewDTO>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ApiResponse.success(reviewService.toDTOList(reviews));
    }

    @GetMapping("/product/{productId}/summary")
    public ApiResponse<ReviewSummaryDTO> getProductReviewSummary(@PathVariable Long productId) {
        return ApiResponse.success(reviewService.getSummary(productId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewsByUser(userId);
        return ApiResponse.success(reviewService.toDTOList(reviews));
    }

    @PostMapping("/{id}/helpful")
    public ApiResponse<ReviewDTO> markHelpful(@PathVariable Long id) {
        Review review = reviewService.markHelpful(id);
        return ApiResponse.success(reviewService.toDTO(review));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.success("Review deleted", null);
    }
}
