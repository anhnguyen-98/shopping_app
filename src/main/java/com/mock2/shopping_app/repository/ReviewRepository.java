package com.mock2.shopping_app.repository;

import com.mock2.shopping_app.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByProduct_ProductId(Long productId, Pageable pageable);
}
