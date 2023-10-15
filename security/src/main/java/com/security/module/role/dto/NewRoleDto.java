package com.security.module.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record NewRoleDto(

        @Length(max = 100, message = "name: can be max 100 characters")
        @NotBlank(message = "name: must not be null or blank")
        String name,

        @NotBlank(message = "description: must not be null or blank")
        String description
) {
}
