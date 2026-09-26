package com.advancedconcept.commonlibrary.controller;

import com.advancedconcept.commonlibrary.common.ApiResponse;
import com.advancedconcept.commonlibrary.common.BaseController;
import com.advancedconcept.commonlibrary.dto.record.GenericPaginationResponse;
import com.advancedconcept.commonlibrary.dto.record.ProductResponse;
import com.advancedconcept.commonlibrary.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController extends BaseController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<GenericPaginationResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "category", required = false) String category
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        return getSuccessResponse(productService.getAllProducts(name, price, category, pageable));
    }
}
