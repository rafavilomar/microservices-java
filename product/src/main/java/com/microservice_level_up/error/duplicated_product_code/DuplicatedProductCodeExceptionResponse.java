package com.microservice_level_up.error.duplicated_product_code;

import java.time.LocalDateTime;

public record DuplicatedProductCodeExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
