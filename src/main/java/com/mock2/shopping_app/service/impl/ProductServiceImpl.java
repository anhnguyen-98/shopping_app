package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.entity.ProductQuantity;
import com.mock2.shopping_app.model.request.ProductDTO;
import com.mock2.shopping_app.model.entity.Product;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.repository.ProductQuantityRepository;
import com.mock2.shopping_app.repository.ProductRepository;
import com.mock2.shopping_app.service.ProductService;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger logger = Logger.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final ProductQuantityRepository productQuantityRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductQuantityRepository productQuantityRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.productQuantityRepository = productQuantityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<Product> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        logger.info("Find all products with pagination and sorting");
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page index must not be less than one");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy));
        org.springframework.data.domain.Page<Product> pagedResult = productRepository.findAll(pageable);
        List<Product> products = new ArrayList<>();
        if (pagedResult.hasContent()) {
            products = pagedResult.getContent();
        }
        Page<Product> productPage = new Page<>();
        productPage.setContent(products);
        productPage.setCurrentPage(pagedResult.getNumber() + 1);
        productPage.setTotalItems(pagedResult.getTotalElements());
        productPage.setTotalPages(pagedResult.getTotalPages());
        return productPage;
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        logger.info("Find product by id");
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product saveProduct(ProductDTO productDTO) {
        Long quantity = productDTO.getProductQuantity();
        logger.info("Saving product to database");
        Product product = modelMapper.map(productDTO, Product.class);
        Product storedProduct = productRepository.save(product);
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProductId(storedProduct.getProductId());
        productQuantity.setQuantity(quantity);
        productQuantity.setProduct(storedProduct);
        storedProduct.setProductQuantity(productQuantity);
        logger.info("Insert product quantity");
        productQuantityRepository.insertProductQuantity(storedProduct.getProductId(), quantity);
        return storedProduct;
    }

    @Override
    public Product updateProduct(Long productId, ProductDTO productDTO) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        logger.info("Update product information");
        Product product = modelMapper.map(productDTO, Product.class);
        product.setProductId(productId);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        logger.info("Trying to delete product with id = " + id);
        productRepository.deleteById(id);
    }
}
