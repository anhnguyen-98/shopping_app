package com.mock2.shopping_app.model.request;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class OrderDTO {
    private List<OrderProductDTO> orderProductList;
}
