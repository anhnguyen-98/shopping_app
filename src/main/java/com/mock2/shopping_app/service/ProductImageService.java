package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.entity.ProductImage;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.response.ProductImageResponse;
import com.mock2.shopping_app.model.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductImageService {
    Page<ProductImage> findAll(Integer pageNo, Integer pageSize, String sortBy);

    void storeProductImage(Long productId, MultipartFile file) throws IOException;

    ProductImage getProductImageById(Long id);

    void deleteProductImage(Long id);

    ProductImageResponse mapProductImageToProductImageResponse(ProductImage productImage);
}
