package com.advancedconcept.commonlibrary.dto.record;

import com.advancedconcept.commonlibrary.dto.pagination.PaginationResponse;

import java.io.Serializable;
import java.util.List;

public record GenericPaginationResponse<T> (
        List<T> data,
        PaginationResponse pagination
) implements Serializable {
}
