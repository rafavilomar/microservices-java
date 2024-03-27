package com.microservice_level_up.dto;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String code,
        double price,
        String name,
        int stock,
        CategoryResponse category
) {
}
