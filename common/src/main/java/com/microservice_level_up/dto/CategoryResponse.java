package com.microservice_level_up.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(
        long id,
        String name,
        String description
) {
}
