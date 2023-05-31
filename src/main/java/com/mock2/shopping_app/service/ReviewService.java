package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.entity.Review;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.ReviewDTO;

public interface ReviewService {

    Page<Review> findAll(Integer pageNo, Integer pageSize, String sortBy);

    Page<Review> findAllByProductId(Long productId, Integer pageNo, Integer pageSize, String sortBy);

    void reviewProduct(Long productId, User user, ReviewDTO reviewDTO);

    void deleteReview(Long id);
}
