package com.mock2.shopping_app.repository;

import com.mock2.shopping_app.model.entity.Order;
import com.mock2.shopping_app.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO orders(user_id, total_cost, order_at) VALUES (:userId, :totalCost, :orderAt)")
    void saveOrder(@Param("userId") Long userid, @Param("totalCost") float totalCost, @Param("orderAt") Instant orderAt);

    @Query("SELECT count(o) > 0 " +
            "FROM Order o " +
            "WHERE o.user.id = :userId " +
            "AND o.status = :status " +
            "AND :productId IN (SELECT op.product.productId FROM OrderProduct op WHERE op.order.orderId = o.orderId)")
    Boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, OrderStatus status);

    Boolean existsByUser_Id(Long userId);
}
