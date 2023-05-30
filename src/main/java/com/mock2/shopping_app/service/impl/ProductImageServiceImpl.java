package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.entity.ProductImage;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.response.ProductImageResponse;
import com.mock2.shopping_app.repository.ProductImageRepository;
import com.mock2.shopping_app.repository.ProductRepository;
import com.mock2.shopping_app.service.ProductImageService;
import com.mock2.shopping_app.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Value("${app.api.path}")
    private String apiPath;

    public ProductImageServiceImpl(ProductImageRepository productImageRepository, ProductRepository productRepository,
                                   ModelMapper modelMapper) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ProductImage> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page index must not be less than one");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy));
        org.springframework.data.domain.Page<ProductImage> pagedResult = productImageRepository.findAll(pageable);
        List<ProductImage> productImages = new ArrayList<>();
        if (pagedResult.hasContent()) {
            productImages = pagedResult.getContent();
        }
        Page<ProductImage> productImagePage = new Page<>();
        productImagePage.setContent(productImages);
        productImagePage.setCurrentPage(pagedResult.getNumber() + 1);
        productImagePage.setTotalItems(pagedResult.getTotalElements());
        productImagePage.setTotalPages(pagedResult.getTotalPages());
        return productImagePage;
    }

    @Override
    public void storeProductImage(Long productId, MultipartFile file) {
        productRepository.findById(productId)
                .map(product -> {
                    try {
                        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                        ProductImage productImage = new ProductImage();
                        productImage.setName(Util.generateRandomUUID() + "_" + fileName);
                        productImage.setType(file.getContentType());
                        productImage.setData(file.getBytes());
                        productImage.setProduct(product);
                        return productImageRepository.save(productImage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new EntityNotFoundException("Product with id = " + productId + " not found"));
    }

    @Override
    public ProductImage getProductImageById(Long id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id = " + id + " not found"));
    }

    @Override
    @Transactional
    public void deleteProductImage(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public ProductImageResponse mapProductImageToProductImageResponse(ProductImage productImage) {
        ProductImageResponse productImageResponse = modelMapper.map(productImage, ProductImageResponse.class);
        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(apiPath + "/productImage/")
                .path(String.valueOf(productImage.getId()))
                .toUriString();
        productImageResponse.setUrl(fileDownloadUri);
        productImageResponse.setSize(productImage.getData().length);
        return productImageResponse;
    }
}
