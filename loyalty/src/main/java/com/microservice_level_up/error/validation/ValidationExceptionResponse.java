package com.microservice_level_up.error.validation;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        List<String> errors) {
}
