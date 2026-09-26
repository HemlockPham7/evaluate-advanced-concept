package com.advancedconcept.commonlibrary.dto.pagination;

public record PaginationResponse (
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
