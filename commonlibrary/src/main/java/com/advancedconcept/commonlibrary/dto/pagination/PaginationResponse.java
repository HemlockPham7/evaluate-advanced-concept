package com.advancedconcept.commonlibrary.dto.pagination;

import java.io.Serializable;

public record PaginationResponse (
        int page,
        int size,
        long totalElements,
        int totalPages
) implements Serializable {
}
