package com.rummikub.controller;

import com.rummikub.entity.response.ApiResponse;
import com.rummikub.exception.CustomException;
import com.rummikub.enums.ExceptionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ExceptionEnum exceptionEnum = e.getExceptionEnum();
        return ResponseEntity.badRequest().body(ApiResponse.fail(exceptionEnum.getCode(), exceptionEnum.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ExceptionEnum.SYSTEM_ERROR.getCode(), ExceptionEnum.SYSTEM_ERROR.getMessage()));
    }
}
