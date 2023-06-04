package com.microservice_level_up.error.not_readable;

import java.time.LocalDateTime;
import java.util.List;

public record NotReadableExceptionResponse(
        LocalDateTime timestamp,
        Integer status,
        String statusDescription,
        List<String> error) {
}