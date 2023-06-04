package com.microservice_level_up.error.not_found;

import java.time.LocalDateTime;

public record NotFoundExceptionResponse(
        LocalDateTime timestamp,
        Integer status,
        String statusDescription,
        String error) {
}
