package com.ecshop.service;

import com.ecshop.dto.ReviewDTO;
import com.ecshop.dto.ReviewSummaryDTO;
import com.ecshop.exception.ReviewNotFoundException;
import com.ecshop.model.Product;
import com.ecshop.model.Review;
import com.ecshop.model.ReviewImage;
import com.ecshop.model.User;
import com.ecshop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    @Transactional
    public Review createReview(Long productId, Long userId, Integer rating, String title, String comment,
                                List<String> imageUrls) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Product product = productService.getProduct(productId);

        Review review = new Review();
        review.setProduct(product);
        User user = new User();
        user.setId(userId);
        review.setUser(user);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);

        if (imageUrls != null) {
            int order = 0;
            for (String url : imageUrls) {
                ReviewImage image = new ReviewImage();
                image.setReview(review);
                image.setImageUrl(url);
                image.setSortOrder(order++);
                review.getImages().add(image);
            }
        }

        return reviewRepository.save(review);
    }

    public Review getReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Transactional
    public Review markHelpful(Long id) {
        Review review = getReview(id);
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        Review review = getReview(id);
        reviewRepository.delete(review);
    }

    public ReviewSummaryDTO getSummary(Long productId) {
        ReviewSummaryDTO summary = new ReviewSummaryDTO();
        summary.setProductId(productId);
        summary.setAverageRating(reviewRepository.findAverageRatingByProductId(productId));
        summary.setTotalReviews(reviewRepository.countByProductId(productId));
        summary.setFiveStarCount(reviewRepository.countByProductIdAndRating(productId, 5));
        summary.setFourStarCount(reviewRepository.countByProductIdAndRating(productId, 4));
        summary.setThreeStarCount(reviewRepository.countByProductIdAndRating(productId, 3));
        summary.setTwoStarCount(reviewRepository.countByProductIdAndRating(productId, 2));
        summary.setOneStarCount(reviewRepository.countByProductIdAndRating(productId, 1));
        return summary;
    }

    public ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setIsVerifiedPurchase(review.getIsVerifiedPurchase());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setImageUrls(review.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()));
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public List<ReviewDTO> toDTOList(List<Review> reviews) {
        return reviews.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
