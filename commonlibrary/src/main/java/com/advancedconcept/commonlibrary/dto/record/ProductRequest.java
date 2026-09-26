package com.advancedconcept.commonlibrary.dto.record;

import java.math.BigDecimal;

public record ProductRequest(
        Long id,
        String name,
        BigDecimal price
) {
}