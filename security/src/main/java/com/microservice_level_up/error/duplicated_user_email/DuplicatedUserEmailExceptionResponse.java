package com.microservice_level_up.error.duplicated_user_email;

import java.time.LocalDateTime;

public record DuplicatedUserEmailExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
