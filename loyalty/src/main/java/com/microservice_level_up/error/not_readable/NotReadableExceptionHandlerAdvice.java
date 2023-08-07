package com.microservice_level_up.error.not_readable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class NotReadableExceptionHandlerAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<NotReadableExceptionResponse> handleErrors(HttpMessageNotReadableException exception) {

        List<String> errors = Arrays.asList(
                "The body request is not readable",
                exception.getMessage()
        );

        NotReadableExceptionResponse response = new NotReadableExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
