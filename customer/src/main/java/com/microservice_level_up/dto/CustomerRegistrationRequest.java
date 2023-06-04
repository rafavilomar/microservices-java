package com.microservice_level_up.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
        String country) {
}
