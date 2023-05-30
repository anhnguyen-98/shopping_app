package com.mock2.shopping_app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Long productId;
    private String name;
    private float price;
    private String description;
    @JsonIgnoreProperties("product")
    private List<ReviewResponse> reviews;
    private ProductQuantityResponse productQuantity;
    @JsonIgnoreProperties("product")
    private List<ProductImageResponse> productImages;
}
