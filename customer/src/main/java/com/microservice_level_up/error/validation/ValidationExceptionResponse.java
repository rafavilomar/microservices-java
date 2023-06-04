package com.microservice_level_up.error.validation;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationExceptionResponse(
        LocalDateTime timestamp,
        Integer status,
        String statusDescription,
        List<String> errors) {
}
