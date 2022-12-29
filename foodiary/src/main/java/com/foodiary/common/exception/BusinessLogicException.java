package com.foodiary.common.exception;

public class BusinessLogicException extends RuntimeException{

    private ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return this.exceptionCode;
    }
}
