package com.foodiary.common.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto(new Date(), exception.getMessage());
        return ResponseEntity.status(exception.getExceptionCode().getStatus()).body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleJwtException(IOException exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException exception) {
        return ResponseEntity.status(401).body(exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMisException(MissingServletRequestPartException exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleJwtException(Exception exception) {
        log.info("error class: {} ", exception.getClass());
        return ResponseEntity.status(200).body(exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissRequestParameterException() {
        return ResponseEntity.status(400).body(ExceptionCode.BAD_REQUEST.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllParameterException() {
        return ResponseEntity.status(400).body(ExceptionCode.BAD_REQUEST.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleSQLIntegrityConstraintViolationException() {
        ExceptionResponseDto response = new ExceptionResponseDto(new Date(), ExceptionCode.SQL_BAD_REQUEST.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<?> errorValid(BindException exception) {
        List<VaildErrorResponseDto> items = new ArrayList<>();
    
        for (ObjectError error : exception.getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            items.add(
                new VaildErrorResponseDto(fieldError.getField(), fieldError.getDefaultMessage())
                );
        }
        return ResponseEntity.status(400).body(items);
    
    }

    // 비밀번호와 한번더 입력한 비밀번호가 일치하지 않을때 에러
    @ExceptionHandler(MorePasswordException.class)
    public ResponseEntity<?> handleMorePasswordException(MorePasswordException exception) {

        VaildErrorResponseDto response = new VaildErrorResponseDto("more_password", exception.getMessage());
        return ResponseEntity.status(400).body(response);
    }

}
