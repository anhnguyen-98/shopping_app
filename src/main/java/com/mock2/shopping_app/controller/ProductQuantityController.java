package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.model.request.ProductQuantityDTO;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.service.ProductQuantityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.path}")
public class ProductQuantityController {
    private final ProductQuantityService productQuantityService;

    public ProductQuantityController(ProductQuantityService productQuantityService) {
        this.productQuantityService = productQuantityService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/product/quantity")
    public ResponseEntity<ApiResponse> modifyProductQuantity(@Valid @RequestBody ProductQuantityDTO productQuantityDTO) {
        productQuantityService.modifyProductQuantity(productQuantityDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Successfully set the quantity = "
                + productQuantityDTO.getQuantity()
                + " for product with id: " + productQuantityDTO.getProductId() + ""));
    }
}
