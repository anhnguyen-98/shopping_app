package com.mock2.shopping_app.model.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class ProductQuantityDTO {
    @Min(value = 0L, message = "The product id must be positive")
    private Long productId;
    @Min(value = 0L, message = "The quantity must be positive")
    private Long quantity;
}
