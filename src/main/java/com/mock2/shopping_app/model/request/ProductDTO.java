package com.mock2.shopping_app.model.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class ProductDTO {
    private String name;
    @Min(value = 0, message = "The price must be positive")
    private float price;
    private String description;
    @Min(value = 0L, message = "The product quantity must be positive")
    private Long productQuantity;
}
