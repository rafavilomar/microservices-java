package com.microservice_level_up.module.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public record BuyProductRequest(

        @NotNull(message = "quantity: must be not null")
        @Positive(message = "quantity: must be higher than 0")
        int quantity,

        @NotBlank(message = "code: must be not null or blank")
        @Size(max = 12, message = "code: must be max 12 characters")
        String code
) {
}
