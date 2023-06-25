package com.microservice_level_up.module.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotNull(message = "id: must be not null")
        @Positive(message = "id: must be higher than zero")
        long id,
        @NotBlank(message = "name: must be not null or blank")
        @Size(max = 32, message = "name: must be max 32 characters")
        String name,
        @NotBlank(message = "description: must be not null or blank")
        String description
) {
}
