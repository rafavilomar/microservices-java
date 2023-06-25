package com.microservice_level_up.error.duplicated_category;

import java.time.LocalDateTime;

public record DuplicatedCategoryExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
