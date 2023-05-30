package com.mock2.shopping_app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageResponse {
    private Long id;
    private String name;
    private String type;
    private String url;
    private long size;
    @JsonIgnoreProperties({"reviews", "productQuantity", "productImages"})
    private ProductResponse product;
}
