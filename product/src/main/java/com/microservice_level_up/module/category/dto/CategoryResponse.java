package com.microservice_level_up.module.category.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(
        long id,
        String name,
        String description
) {
}
