package com.microservice_level_up.error.duplicated_user_email;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class DuplicatedUserEmailExceptionHandlerAdvice {

    @ExceptionHandler(DuplicatedUserEmailException.class)
    public ResponseEntity<DuplicatedUserEmailExceptionResponse> handleError(DuplicatedUserEmailException exception) {

        DuplicatedUserEmailExceptionResponse response = new DuplicatedUserEmailExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
