package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.entity.Product;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.request.ProductDTO;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.ProductImageResponse;
import com.mock2.shopping_app.model.response.ProductResponse;
import com.mock2.shopping_app.service.ProductImageService;
import com.mock2.shopping_app.service.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.path}")
public class ProductController {
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ModelMapper modelMapper,
                             ProductImageService productImageService) {
        this.productService = productService;
        this.modelMapper = modelMapper;
        this.productImageService = productImageService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam(defaultValue = "1", name = "pageNo") Integer pageNo,
            @RequestParam(defaultValue = "5", name = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "productId", name = "sortBy") String sortBy
    ) {
        Page<Product> productPage = productService.findAll(pageNo, pageSize, sortBy);
        List<ProductResponse> productResponseList = productPage.getContent().stream()
                .map(product -> {
                    ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
                    List<ProductImageResponse> productImageResponses = product.getProductImages().stream()
                            .map(productImageService::mapProductImageToProductImageResponse).toList();
                    productResponse.setProductImages(productImageResponses);
                    return productResponse;
                })
                .collect(Collectors.toList());
        Page<ProductResponse> productResponsePage = new Page<>();
        productResponsePage.setContent(productResponseList);
        productResponsePage.setCurrentPage(productPage.getCurrentPage());
        productResponsePage.setTotalItems(productPage.getTotalItems());
        productResponsePage.setTotalPages(productPage.getTotalPages());
        return ResponseEntity.ok(productResponsePage);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(product -> ResponseEntity.ok(modelMapper.map(product, ProductResponse.class)))
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product")
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(modelMapper.map(productService.saveProduct(productDTO), ProductResponse.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/product/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(modelMapper.map(productService.updateProduct(id, productDTO), ProductResponse.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/product/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse(true, "Successfully delete product with id: " + id));
    }
}
