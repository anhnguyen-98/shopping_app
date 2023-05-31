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
import org.apache.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final Logger logger = Logger.getLogger(ReviewServiceImpl.class);
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
        logger.info("Find all reviews with pagination and sorting");
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
        logger.info("Find all orders by product id with pagination and sorting");
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
        logger.info("Starting to save review");
        if (orderService.existsByUserIdAndProductIdAndStatus(user.getId(), productId, OrderStatus.DELIVERED)) {
            logger.info("Trying to save review");
            reviewRepository.save(Review.builder()
                            .review(reviewDTO.getReview())
                            .product(product)
                            .user(user).build());
        } else {
            logger.error("User is not allowed to review");
            throw new InvalidToReviewProductException(productId);
        }
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with id: " + id);
        }
        logger.info("Trying to delete review with id: " + id);
        reviewRepository.deleteById(id);
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
