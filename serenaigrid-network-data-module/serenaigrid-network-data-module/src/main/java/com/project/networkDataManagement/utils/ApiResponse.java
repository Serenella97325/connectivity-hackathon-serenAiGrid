package com.project.networkDataManagement.utils;

import java.util.List;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private List<String> errorMessages;

    // constructors
    public ApiResponse(boolean success, T data, List<String> errorMessages) {
        this.success = success;
        this.data = data;
        this.errorMessages = errorMessages;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(List<String> errorMessages) {
        return new ApiResponse<>(false, null, errorMessages);
    }

    public static <T> ApiResponse<T> failure(String errorMessage) {
        return new ApiResponse<>(false, null, List.of(errorMessage));
    }

    // Getter e Setter
    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
