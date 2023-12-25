package com.customer.module.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRegistrationRequest(
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
