package com.microservice_level_up.error.duplicated_product_code;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class DuplicatedProductCodeExceptionHandlerAdvice {

    @ExceptionHandler(DuplicatedProductCodeException.class)
    public ResponseEntity<DuplicatedProductCodeExceptionResponse> handleError(DuplicatedProductCodeException exception) {

        DuplicatedProductCodeExceptionResponse response = new DuplicatedProductCodeExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
