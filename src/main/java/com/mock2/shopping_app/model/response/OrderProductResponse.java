package com.mock2.shopping_app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductResponse {
    private Long orderProductId;
    private int quantity;
    private int price;
    @JsonIgnore
    private OrderResponse order;
    @JsonIgnoreProperties({"reviews", "productQuantity"})
    private ProductResponse product;
}
