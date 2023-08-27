package com.microservice_level_up.error.conflict;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class DuplicatedCategoryExceptionHandlerAdvice {

    @ExceptionHandler(DuplicatedCategoryException.class)
    public ResponseEntity<DuplicatedCategoryExceptionResponse> handleError(DuplicatedCategoryException exception) {

        DuplicatedCategoryExceptionResponse response = new DuplicatedCategoryExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
