package com.microservice_level_up.error.not_active_points_redemption_rule;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class NotActivePointsRedemptionRuleExceptionHandlerAdvice {

    @ExceptionHandler(NotActivePointsRedemptionRuleException.class)
    public ResponseEntity<NotActivePointsRedemptionRuleExceptionResponse> handleError(NotActivePointsRedemptionRuleException exception) {

        NotActivePointsRedemptionRuleExceptionResponse response = new NotActivePointsRedemptionRuleExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
