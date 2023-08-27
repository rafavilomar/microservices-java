package com.microservice_level_up.module.user.dto;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public record NewUserDto(
        @NotBlank(message = "email: must not be null or blank")
        String email,

        @NotBlank(message = "fullName: must not be null or blank")
        String fullName,

        @NotBlank(message = "password: must not be null or blank")
        String password
) {
}
