package com.volcano.domain;

import lombok.Data;

@Data
public class ApiValidationError {
    private String field;
    private String message;

    public ApiValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}