package com.mock2.shopping_app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mock2.shopping_app.model.entity.OrderProduct;
import com.mock2.shopping_app.model.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long orderId;
    private float totalCost;
    private Instant orderAt;
    private OrderStatus status;
    private UserResponse user;
    @JsonIgnoreProperties({"reviews", "productQuantity"})
    private List<ProductResponse> orderedProducts;
    private List<OrderProductResponse> orderProductList;
}
