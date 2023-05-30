package com.mock2.shopping_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidToReviewProductException extends RuntimeException {
    private final Long productId;

    public InvalidToReviewProductException(Long productId) {
        super(String.format("You have not bought the product " + productId
                + " or your order for this product is not delivered yet"));
        this.productId = productId;
    }
}
