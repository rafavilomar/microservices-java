package com.microservice_level_up.module.customer.dto;

import jakarta.validation.constraints.*;

public record CustomerUpdateRequest(
        @NotNull(message = "id: must not be null")
        @Positive(message = "id: must be higher than zero")
        Long id,

        @NotBlank(message = "firstName: must not be null or blank")
        @Size(max = 64, message = "firstName: must be max 64 characters")
        String firstName,

        @Size(max = 64, message = "lastName: must be max 64 characters")
        @NotBlank(message = "lastName: must not be null or blank")
        String lastName,

        @NotBlank(message = "email: must not be null or blank")
        @Email(message = "email: must be a valid format")
        String email,

        @NotBlank(message = "country: must not be null or blank")
        String country,

        @NotBlank(message = "address: must not be null or blank")
        String address) {
}
