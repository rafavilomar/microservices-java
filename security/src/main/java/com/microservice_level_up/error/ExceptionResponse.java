package com.microservice_level_up.error;

import java.time.LocalDateTime;
import java.util.List;

public record ExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        List<String> error) {
}
