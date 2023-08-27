package com.microservice_level_up.error.not_enough_points;

import java.time.LocalDateTime;

public record DuplicatedProductCodeExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
