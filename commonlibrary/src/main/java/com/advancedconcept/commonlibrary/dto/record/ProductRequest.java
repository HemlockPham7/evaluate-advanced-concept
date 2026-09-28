package com.advancedconcept.commonlibrary.dto.record;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price,
        String category,
        String status
) {
}