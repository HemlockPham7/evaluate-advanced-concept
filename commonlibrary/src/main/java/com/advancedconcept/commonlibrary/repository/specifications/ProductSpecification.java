package com.advancedconcept.commonlibrary.repository.specifications;

import com.advancedconcept.commonlibrary.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private static final String FIELD_NAME = "name";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_CATEGORY = "category";

    public static Specification<Product> filterProducts(String name, BigDecimal price, String category) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(FIELD_NAME)), "%" + name.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(FIELD_CATEGORY)), "%" + category.toLowerCase() + "%"));
            }

            if (price != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(FIELD_PRICE), price));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
