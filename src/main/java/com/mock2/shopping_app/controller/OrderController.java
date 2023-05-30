package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.entity.Order;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.OrderDTO;
import com.mock2.shopping_app.model.request.OrderStatusRequest;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.OrderResponse;
import com.mock2.shopping_app.security.CustomUserDetails;
import com.mock2.shopping_app.service.OrderService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.path}")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/order")
    @Transactional
    public ResponseEntity<ApiResponse> placeOrder(@Valid @RequestBody OrderDTO orderDTO,
                                                  @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        orderService.placeOrder(user, orderDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Successfully place the order"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> findAll(
            @RequestParam(defaultValue = "1", name = "pageNo") Integer pageNo,
            @RequestParam(defaultValue = "5", name = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "orderId", name = "sortBy") String sortBy
    ) {
        Page<Order> orderPage = orderService.findAll(pageNo, pageSize, sortBy);
        List<OrderResponse> orderResponseList = orderPage.getContent().stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
        Page<OrderResponse> orderResponsePage = new Page<>();
        orderResponsePage.setContent(orderResponseList);
        orderResponsePage.setCurrentPage(orderPage.getCurrentPage());
        orderResponsePage.setTotalItems(orderPage.getTotalItems());
        orderResponsePage.setTotalPages(orderPage.getTotalPages());
        return ResponseEntity.ok(orderResponsePage);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id)
                .map(order -> ResponseEntity.ok(modelMapper.map(order, OrderResponse.class)))
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/order/{orderId}/setStatus")
    public ResponseEntity<ApiResponse> setOrderStatus(
            @PathVariable(name = "orderId") Long orderId,
            @Valid @RequestBody OrderStatusRequest orderStatusRequest) {
        orderService.setOrderStatus(orderId, orderStatusRequest.getStatus());
        return ResponseEntity.ok(new ApiResponse(true, "Successfully set order status"));
    }
}
