package com.advancedconcept.commonlibrary.common;

public abstract class BaseController {

    protected <T> ApiResponse<T> createSuccessResponse(T data) {
        return ApiResponse.createdSuccessfully(data);
    }

    protected <T> ApiResponse<T> getSuccessResponse(T data) {
        return ApiResponse.success(data);
    }
}
