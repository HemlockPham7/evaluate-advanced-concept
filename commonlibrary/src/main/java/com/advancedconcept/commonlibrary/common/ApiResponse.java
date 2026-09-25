package com.advancedconcept.commonlibrary.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int code, String message, T result) {

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(200, "Success", result);
    }

    public static <T> ApiResponse<T> createdSuccessfully(T result) {
        return new ApiResponse<>(201, "Success", result);
    }

    public static <T> ApiResponse<T> errorListDataMessage(int code, String message, T result) {
        return new ApiResponse<>(code, message, result);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
