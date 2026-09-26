package com.advancedconcept.commonlibrary.dto.record;

import com.advancedconcept.commonlibrary.dto.pagination.PaginationResponse;

import java.util.List;

public record GenericPaginationResponse<T> (
        List<T> data,
        PaginationResponse pagination
) {
}
