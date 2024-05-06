package com.microservice_level_up.error;

import java.util.List;

public record ExceptionResponse(
        int status,
        String statusDescription,
        List<String> error) {
}
