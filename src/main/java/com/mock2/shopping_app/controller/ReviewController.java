package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.model.entity.Review;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.ReviewDTO;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.ReviewResponse;
import com.mock2.shopping_app.security.CustomUserDetails;
import com.mock2.shopping_app.service.ReviewService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.path}")
public class ReviewController {
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    public ReviewController(ReviewService reviewService, ModelMapper modelMapper) {
        this.reviewService = reviewService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewResponse>> findAll(
            @RequestParam(defaultValue = "1", name = "pageNo") Integer pageNo,
            @RequestParam(defaultValue = "5", name = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "reviewId", name = "sortBy") String sortBy
    ) {

        Page<Review> reviewPage = reviewService.findAll(pageNo, pageSize, sortBy);
        List<ReviewResponse> reviewResponseList = reviewPage.getContent().stream()
                .map(review -> modelMapper.map(review, ReviewResponse.class))
                .toList();
        return ResponseEntity.ok(setReviewResponsePage(reviewResponseList, reviewPage));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/product/{productId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> findAllByProductId(
            @PathVariable(name = "productId") Long productId,
            @RequestParam(defaultValue = "1", name = "pageNo") Integer pageNo,
            @RequestParam(defaultValue = "5", name = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "reviewId", name = "sortBy") String sortBy
    ) {

        Page<Review> reviewPage = reviewService.findAllByProductId(productId, pageNo, pageSize, sortBy);
        List<ReviewResponse> reviewResponseList = reviewPage.getContent().stream()
                .map(review -> modelMapper.map(review, ReviewResponse.class))
                .toList();
        return ResponseEntity.ok(setReviewResponsePage(reviewResponseList, reviewPage));
    }

    private Page<ReviewResponse> setReviewResponsePage(List<ReviewResponse> reviewResponseList,
                                                       Page<Review> reviewPage) {
        Page<ReviewResponse> reviewResponsePage = new Page<>();
        reviewResponsePage.setContent(reviewResponseList);
        reviewResponsePage.setTotalPages(reviewPage.getTotalPages());
        reviewResponsePage.setTotalItems(reviewPage.getTotalItems());
        reviewResponsePage.setCurrentPage(reviewPage.getCurrentPage());
        return reviewResponsePage;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/product/{productId}/review")
    public ResponseEntity<ApiResponse> makeReviewForProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ReviewDTO reviewDTO
            ) {
        reviewService.reviewProduct(productId, currentUser.getUser(), reviewDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Successfully review product with id: " + productId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/review/{id}")
    public ResponseEntity<ApiResponse> deleteReviewById(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(new ApiResponse(true, "Successfully delete review with id: " + id));
    }
}