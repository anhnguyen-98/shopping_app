package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.entity.Order;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.enums.OrderStatus;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.OrderDTO;

import java.util.Optional;

public interface OrderService {

    void placeOrder(User user, OrderDTO orderDTO);

    Page<Order> findAll(Integer pageNo, Integer pageSize, String sortBy);

    Optional<Order> findOrderById(Long id);

    Boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, OrderStatus status);

    void setOrderStatus(Long orderId, String status);

    void deleteOrder(Long id);
}
