package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.entity.ProductQuantity;
import com.mock2.shopping_app.model.request.ProductQuantityDTO;
import com.mock2.shopping_app.repository.ProductQuantityRepository;
import com.mock2.shopping_app.service.ProductQuantityService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ProductQuantityServiceImpl implements ProductQuantityService {
    private final Logger logger = Logger.getLogger(ProductQuantityServiceImpl.class);
    private final ProductQuantityRepository productQuantityRepository;

    public ProductQuantityServiceImpl(ProductQuantityRepository productQuantityRepository) {
        this.productQuantityRepository = productQuantityRepository;
    }

    @Override
    public void modifyProductQuantity(ProductQuantityDTO productQuantityDTO) {
        ProductQuantity existingProductQuantity = productQuantityRepository
                .findByProductId(productQuantityDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product quantity not found for productId: "
                        + productQuantityDTO.getProductId()));
        logger.info("Modifying product quantity");
        existingProductQuantity.setQuantity(productQuantityDTO.getQuantity());
        productQuantityRepository.save(existingProductQuantity);
    }
}
