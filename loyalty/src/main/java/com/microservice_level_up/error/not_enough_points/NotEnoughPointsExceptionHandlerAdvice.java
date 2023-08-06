package com.microservice_level_up.error.not_enough_points;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class NotEnoughPointsExceptionHandlerAdvice {

    @ExceptionHandler(NotEnoughPointsException.class)
    public ResponseEntity<NotEnoughPointsExceptionResponse> handleError(NotEnoughPointsException exception) {

        NotEnoughPointsExceptionResponse response = new NotEnoughPointsExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
