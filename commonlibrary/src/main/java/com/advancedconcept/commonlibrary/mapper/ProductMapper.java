package com.advancedconcept.commonlibrary.mapper;

import com.advancedconcept.commonlibrary.dto.record.ProductResponse;
import com.advancedconcept.commonlibrary.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductResponse toResponse(Product product);
}
