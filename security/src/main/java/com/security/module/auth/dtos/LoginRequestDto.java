package com.security.module.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull
        @NotBlank
        String username,
        String password
) {
}
