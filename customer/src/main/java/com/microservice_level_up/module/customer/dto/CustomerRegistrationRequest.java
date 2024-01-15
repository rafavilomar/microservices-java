package com.microservice_level_up.module.customer.dto;

import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
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
        String address,

        @NotNull(message = "idUser: must not be null")
        Long idUser) {
}
