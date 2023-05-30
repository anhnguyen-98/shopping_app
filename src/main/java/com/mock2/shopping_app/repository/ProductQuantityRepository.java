package com.mock2.shopping_app.repository;

import com.mock2.shopping_app.model.entity.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, Long> {

    Optional<ProductQuantity> findByProductId(Long productId);

    @Modifying
    @Query("INSERT INTO ProductQuantity(productId, quantity) VALUES (:productId, :quantity)")
    void insertProductQuantity(@Param("productId") Long productId, @Param("quantity") Long quantity);
}
