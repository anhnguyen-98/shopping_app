package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.request.ProductDTO;
import com.mock2.shopping_app.model.entity.Product;
import com.mock2.shopping_app.model.other.Page;

import java.util.Optional;

public interface ProductService {

    Page<Product> findAll(Integer pageNo, Integer pageSize, String sortBy);

    Optional<Product> findProductById(Long id);

    Product saveProduct(ProductDTO productDTO);

    Product updateProduct(Long productId, ProductDTO productDTO);

    void deleteProduct(Long id);
}
