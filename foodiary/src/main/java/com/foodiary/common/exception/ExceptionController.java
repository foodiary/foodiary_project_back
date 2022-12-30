package com.foodiary.common.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Date;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException exception, WebRequest request) {
        ExceptionResponseDto response = new ExceptionResponseDto(new Date(), exception.getMessage(), request.getDescription(false));
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleJwtException(Exception exception) {
        return ResponseEntity.status(200).body(exception.getMessage());
    }
}
