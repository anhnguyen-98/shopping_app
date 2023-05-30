package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.model.entity.ProductImage;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.ProductImageResponse;
import com.mock2.shopping_app.service.ProductImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${app.api.path}")
public class ProductImageController {
    private final ProductImageService productImageService;
    @Value("${app.api.path}")
    private String apiPath;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/productImages")
    public ResponseEntity<Page<ProductImageResponse>> findAll(
            @RequestParam(defaultValue = "1", name = "pageNo") Integer pageNo,
            @RequestParam(defaultValue = "5", name = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "id", name = "sortBy") String sortBy
    ) {
        Page<ProductImage> productImagePage = productImageService.findAll(pageNo, pageSize, sortBy);
        List<ProductImageResponse> productImageResponses = productImagePage.getContent().stream()
                .map(productImageService::mapProductImageToProductImageResponse)
                .toList();
        Page<ProductImageResponse> productImageResponsePage = new Page<>();
        productImageResponsePage.setContent(productImageResponses);
        productImageResponsePage.setCurrentPage(productImagePage.getCurrentPage());
        productImageResponsePage.setTotalItems(productImagePage.getTotalItems());
        productImageResponsePage.setTotalPages(productImagePage.getTotalPages());
        return ResponseEntity.ok(productImageResponsePage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product/{productId}/productImage")
    public ResponseEntity<ApiResponse> uploadProductImage(@PathVariable("productId") Long productId,
                                                          @RequestParam("file") MultipartFile file) {
        String message;
        try {
            productImageService.storeProductImage(productId, file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.ok(new ApiResponse(true, message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ApiResponse(false, message));
        }
    }

    @GetMapping("/productImage/{id}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        ProductImage productImage = productImageService.getProductImageById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + productImage.getName() + "\"")
                .body(productImage.getData());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/productImage/{id}")
    public ResponseEntity<ApiResponse> deleteProductImage(@PathVariable Long id) {
        try {
            productImageService.deleteProductImage(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully delete product image with id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse(false, "Could not delete product image with id: " + id));
        }
    }
}
