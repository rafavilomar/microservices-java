package com.microservice_level_up.module.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record CategoryRegistrationRequest(
        @NotBlank(message = "name: must be not null or blank")
        @Size(max = 32, message = "name: must be max 32 characters")
        String name,
        @NotBlank(message = "description: must be not null or blank")
        String description
) {
}
