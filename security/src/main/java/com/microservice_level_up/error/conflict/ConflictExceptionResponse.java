package com.microservice_level_up.error.conflict;

import java.time.LocalDateTime;

public record ConflictExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
