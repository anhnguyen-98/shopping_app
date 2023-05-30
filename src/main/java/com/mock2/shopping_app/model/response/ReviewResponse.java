package com.mock2.shopping_app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponse {
    private Long reviewId;
    private String review;
    @JsonIgnoreProperties({"reviews"})
    private ProductResponse product;
    @JsonIncludeProperties({"id", "email", "firstName", "lastName"})
    private UserResponse user;
}
