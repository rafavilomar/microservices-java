package com.microservice_level_up.module.product.dto;

import com.microservice_level_up.module.category.dto.CategoryResponse;
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
