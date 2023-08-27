package com.microservice_level_up.error.conflict;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ConflictExceptionHandlerAdvice {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ConflictExceptionResponse> handleError(ConflictException exception) {

        ConflictExceptionResponse response = new ConflictExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
