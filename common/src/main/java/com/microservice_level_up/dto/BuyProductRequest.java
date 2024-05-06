package com.microservice_level_up.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BuyProductRequest(

        @NotNull(message = "quantity: must be not null")
        @Positive(message = "quantity: must be higher than 0")
        int quantity,

        @NotBlank(message = "code: must be not null or blank")
        @Size(max = 12, message = "code: must be max 12 characters")
        String code,

        @NotNull(message = "price: must be not null")
        double price
) {
}
