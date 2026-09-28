package com.advancedconcept.bookservice.dto.record;

import java.math.BigDecimal;

public record BookResponse (
        Long id,
        String name,
        String content,
        String author,
        BigDecimal price,
        String category,
        String status
) {
}
