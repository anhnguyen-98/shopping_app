package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.entity.Order;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.enums.OrderStatus;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.OrderDTO;
import com.mock2.shopping_app.repository.OrderProductRepository;
import com.mock2.shopping_app.repository.OrderRepository;
import com.mock2.shopping_app.service.OrderService;
import org.apache.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final Logger logger = Logger.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    @Transactional
    public void placeOrder(User user, OrderDTO orderDTO) {
        Order newOrder = new Order();
        newOrder.setOrderAt(Instant.now());
        newOrder.setUser(user);
        float totalCost = (float) orderDTO.getOrderProductList().stream()
                .mapToDouble(value -> value.getPrice() * value.getQuantity())
                .sum();
        newOrder.setTotalCost(totalCost);
        logger.info("Trying to store order into the order table");
        Order storedOrder = orderRepository.save(newOrder);
        logger.info("Trying to insert order product into the order_product table");
        orderDTO.getOrderProductList().forEach(orderProductDTO -> {
            orderProductRepository.insertOrderProduct(storedOrder.getOrderId(), orderProductDTO.getProductId(),
                    orderProductDTO.getQuantity(), orderProductDTO.getPrice());
        });
    }

    @Override
    public Page<Order> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        logger.info("Find all orders with pagination and sorting");
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page index must not be less than one");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy));
        org.springframework.data.domain.Page<Order> pagedResult = orderRepository.findAll(pageable);
        List<Order> orders = new ArrayList<>();
        if (pagedResult.hasContent()) {
            orders = pagedResult.getContent();
        }
        Page<Order> orderPage = new Page<>();
        orderPage.setContent(orders);
        orderPage.setCurrentPage(pagedResult.getNumber() + 1);
        orderPage.setTotalItems(pagedResult.getTotalElements());
        orderPage.setTotalPages(pagedResult.getTotalPages());
        return orderPage;
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        logger.info("Find order by id");
        return orderRepository.findById(id);
    }

    @Override
    public Boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, OrderStatus status) {
        logger.info("Check if order exists by user id, product id and order status");
        return orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, status);
    }

    @Override
    public void setOrderStatus(Long orderId, String status) {
        logger.info("Trying to set order status");
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(orderStatus);
                    return order;
                })
                .map(orderRepository::save)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        logger.info("Trying to delete order with id: " + id);
        orderRepository.deleteById(id);
    }


}
