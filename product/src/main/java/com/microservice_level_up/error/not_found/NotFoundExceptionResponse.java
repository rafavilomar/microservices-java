package com.microservice_level_up.error.not_found;

import java.time.LocalDateTime;

public record NotFoundExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
