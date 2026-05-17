package com.rummikub.exception;

import com.rummikub.enums.ExceptionEnum;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionEnum exceptionEnum;

    public CustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

}
