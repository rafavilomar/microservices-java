package com.microservice_level_up.error.invalid_expiration_date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class InvalidCardExpirationDateExceptionHandlerAdvice {

    @ExceptionHandler(InvalidCardExpirationDateException.class)
    public ResponseEntity<InvalidCardExpirationDateExceptionResponse> handleError(InvalidCardExpirationDateException exception) {

        InvalidCardExpirationDateExceptionResponse response = new InvalidCardExpirationDateExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
