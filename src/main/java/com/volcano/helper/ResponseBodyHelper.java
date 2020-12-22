package com.volcano.helper;

import com.volcano.domain.ApiError;
import com.volcano.domain.ApiValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseBodyHelper {
    public static <T extends ResponseEntity<T>> ResponseEntity<T> ok(Object body) {
        return new ResponseEntity(body, HttpStatus.OK);
    }

    public static <T extends ResponseEntity<T>> ResponseEntity<T> created(Object body) {
        return new ResponseEntity(body, HttpStatus.CREATED);
    }

    public static <T extends ResponseEntity<T>> ResponseEntity<T> badRequest(List<ApiValidationError> errors) {
        return new ResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, errors), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity buildResponseEntity(ApiError apiError) {
        return new ResponseEntity(apiError, apiError.getStatus());
    }
}
