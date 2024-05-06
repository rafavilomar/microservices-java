package com.microservice_level_up.module.product.dto;

import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Builder
public record ProductRegistrationRequest(
        @NotBlank(message = "code: must be not null or blank")
        @Size(max = 12, message = "code: must be max 12 characters")
        String code,

        @NotNull(message = "price: must be not null")
        @Positive(message = "price: must be higher than 0")
        double price,

        @NotBlank(message = "name: must be not null or blank")
        @Size(max = 64, message = "name: must be max 64 characters")
        String name,

        @NotNull(message = "stock: must be not null")
        @Positive(message = "stock: must be higher than 0")
        int stock,

        @NotNull(message = "categoryId: must be not null")
        long categoryId
) {
}
