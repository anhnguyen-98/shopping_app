package com.mock2.shopping_app.repository;

import com.mock2.shopping_app.model.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO order_product(order_id, product_id, product_quantity, price) " +
            "VALUES (:orderId, :productId, :quantity, :price)")
    void insertOrderProduct(@Param("orderId") Long orderId, @Param("productId") Long productId,
                            @Param("quantity") int quantity, @Param("price") float price);
}
