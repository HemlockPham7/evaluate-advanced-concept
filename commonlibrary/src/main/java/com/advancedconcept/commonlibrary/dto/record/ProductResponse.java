package com.advancedconcept.commonlibrary.dto.record;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        String category
) implements Serializable {
}