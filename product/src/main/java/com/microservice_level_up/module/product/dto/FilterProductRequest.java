package com.microservice_level_up.module.product.dto;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Builder
public record FilterProductRequest(

        String productCode,
        String productName,
        String categoryName,

        @NotNull(message = "page: must be not null")
        @Positive(message = "page: must be higher than zero")
        int page,

        @NotNull(message = "size: must be not null")
        @Positive(message = "size: must be higher than zero")
        int size
) {
}
