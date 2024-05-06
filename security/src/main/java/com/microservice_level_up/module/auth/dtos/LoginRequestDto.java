package com.microservice_level_up.module.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull
        @Email
        String email,
        @NotNull
        String password
) {
}
