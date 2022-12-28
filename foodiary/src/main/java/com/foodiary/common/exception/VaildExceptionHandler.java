package com.foodiary.common.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class VaildExceptionHandler {
    
    @ExceptionHandler({BindException.class})
    public ResponseEntity<?> errorValid(BindException exception) {
        List<VaildErrorDto> items = new ArrayList<>();
    
        for (ObjectError error : exception.getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            items.add(
                new VaildErrorDto(fieldError.getField(), fieldError.getDefaultMessage(), HttpStatus.BAD_REQUEST.value())
                );
        }
    
    return new ResponseEntity<>(items, HttpStatus.BAD_REQUEST);
    }
    
}
