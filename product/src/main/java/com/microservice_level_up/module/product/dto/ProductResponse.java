package com.microservice_level_up.module.product.dto;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String code,
        double price,
        String name,
        int stock
) {
}
