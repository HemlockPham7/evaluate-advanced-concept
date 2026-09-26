package com.advancedconcept.commonlibrary.service;

import com.advancedconcept.commonlibrary.dto.record.GenericPaginationResponse;
import com.advancedconcept.commonlibrary.dto.record.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    GenericPaginationResponse<ProductResponse> getAllProducts(String name, BigDecimal price, String category, Pageable pageable);
}
