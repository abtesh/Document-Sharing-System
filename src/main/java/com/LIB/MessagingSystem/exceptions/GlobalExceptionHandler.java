package com.LIB.MessagingSystem.exceptions;

import com.LIB.MessagingSystem.Dto.SecurityDtos.GenericResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<GenericResponseDto<Void>> handleAccountBlockedException(AccountBlockedException ex) {
        return new ResponseEntity<>(new GenericResponseDto<>(false,403,ex.getMessage(),null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericResponseDto<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(new GenericResponseDto<>(false,403,ex.getMessage(),null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GenericResponseDto<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(new GenericResponseDto<>(false,403,ex.getMessage(),null), HttpStatus.FORBIDDEN);
    }
}
