package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.exception.InvalidToReviewProductException;
import com.mock2.shopping_app.model.entity.Product;
import com.mock2.shopping_app.model.entity.Review;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.enums.OrderStatus;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.ReviewDTO;
import com.mock2.shopping_app.repository.ReviewRepository;
import com.mock2.shopping_app.service.OrderService;
import com.mock2.shopping_app.service.ProductService;
import com.mock2.shopping_app.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, OrderService orderService, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.orderService = orderService;
        this.productService = productService;
    }

    @Override
    public Page<Review> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page index must not be less than one");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy));
        org.springframework.data.domain.Page<Review> pagedResult = reviewRepository.findAll(pageable);
        List<Review> reviews = new ArrayList<>();
        if (pagedResult.hasContent()) {
            reviews = pagedResult.getContent();
        }
        return setReviewPage(reviews, pagedResult);
    }

    @Override
    public Page<Review> findAllByProductId(Long productId, Integer pageNo, Integer pageSize, String sortBy) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page index must not be less than one");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy));
        org.springframework.data.domain.Page<Review> pagedResult
                = reviewRepository.findAllByProduct_ProductId(productId, pageable);
        List<Review> reviewsByProduct = new ArrayList<>();
        if (pagedResult.hasContent()) {
            reviewsByProduct = pagedResult.getContent();
        }
        return setReviewPage(reviewsByProduct, pagedResult);
    }

    @Override
    @Transactional
    public void reviewProduct(Long productId, User user, ReviewDTO reviewDTO) {
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        if (orderService.existsByUserIdAndProductIdAndStatus(user.getId(), productId, OrderStatus.DELIVERED)) {
            reviewRepository.save(Review.builder()
                            .review(reviewDTO.getReview())
                            .product(product)
                            .user(user).build());
        } else {
            throw new InvalidToReviewProductException(productId);
        }
    }

    private Page<Review> setReviewPage(List<Review> reviews, org.springframework.data.domain.Page<Review> pagedResult) {
        Page<Review> reviewPage = new Page<>();
        reviewPage.setContent(reviews);
        reviewPage.setTotalPages(pagedResult.getTotalPages());
        reviewPage.setTotalItems(pagedResult.getTotalElements());
        reviewPage.setCurrentPage(pagedResult.getNumber() + 1);
        return reviewPage;
    }
}
