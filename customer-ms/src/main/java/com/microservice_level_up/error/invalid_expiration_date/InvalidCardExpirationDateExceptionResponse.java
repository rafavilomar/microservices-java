package com.microservice_level_up.error.invalid_expiration_date;

import java.time.LocalDateTime;

public record InvalidCardExpirationDateExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
