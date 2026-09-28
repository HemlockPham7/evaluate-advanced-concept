package com.advancedconcept.commonlibrary.dto.record;

import java.math.BigDecimal;

public record BookRequest (
        String name,
        String content,
        BigDecimal price,
        String category,
        String author
) {
}