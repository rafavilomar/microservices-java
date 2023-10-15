package com.security.module.user.dto;

import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Builder
public record NewUserDto(
        @NotBlank(message = "email: must not be null or blank")
        String email,

        @NotBlank(message = "fullName: must not be null or blank")
        String fullName,

        @NotBlank(message = "password: must not be null or blank")
        String password,

        @NotNull(message = "idRole: must not be null")
        @Positive(message = "idRole: must be higher than 0")
        long idRole
) {
}
