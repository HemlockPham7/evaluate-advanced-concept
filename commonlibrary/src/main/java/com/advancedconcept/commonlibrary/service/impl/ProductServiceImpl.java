package com.advancedconcept.commonlibrary.service.impl;

import com.advancedconcept.commonlibrary.dto.pagination.PaginationResponse;
import com.advancedconcept.commonlibrary.dto.record.GenericPaginationResponse;
import com.advancedconcept.commonlibrary.dto.record.ProductResponse;
import com.advancedconcept.commonlibrary.entity.Product;
import com.advancedconcept.commonlibrary.mapper.ProductMapper;
import com.advancedconcept.commonlibrary.repository.ProductRepository;
import com.advancedconcept.commonlibrary.repository.specifications.ProductSpecification;
import com.advancedconcept.commonlibrary.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public GenericPaginationResponse<ProductResponse> getAllProducts(String name, BigDecimal price, String category, Pageable pageable) {
        // Page<Product> products = productRepository.findAll(pageable);
        // return products.map(product -> productMapper.toResponse(product));

        // Custom query
        // Page<Product> products = productRepository.findActiveByCategory(category, pageable);

        // Jpa specification
        Page<Product> products = productRepository.findAll(
                ProductSpecification.filterProducts(name, price, category),
                pageable
        );
        PaginationResponse pagination = new PaginationResponse(
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages()
        );
        return new GenericPaginationResponse<>(
                products.map(productMapper::toResponse).getContent(),
                pagination
        );
    }


}
