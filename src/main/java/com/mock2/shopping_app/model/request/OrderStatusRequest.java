package com.mock2.shopping_app.model.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {
    @Pattern(regexp = "^(IN_DELIVERING|DELIVERED)$", message = "Not a valid status")
    private String status;
}
