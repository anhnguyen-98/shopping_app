package com.mock2.shopping_app.model.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class OrderProductDTO {
    @Min(value = 0L, message = "The product id must be positive")
    private Long productId;
    @Min(value = 0, message = "The quantity must be positive")
    private int quantity;
    @Min(value = 0, message = "The price must be positive")
    private int price;
    @Min(value = 0L, message = "The order id must be positive")
    private Long orderId;
}
