package com.security.module.role.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Builder
public record UpdateRoleDto(

        @NotNull(message = "id: must not be null")
        @Positive(message = "id: must be higher than 0")
        long id,

        @Length(max = 100, message = "name: can be max 100 characters")
        @NotBlank(message = "name: must not be null or blank")
        String name,

        @NotBlank(message = "description: must not be null or blank")
        String description
) {
}
