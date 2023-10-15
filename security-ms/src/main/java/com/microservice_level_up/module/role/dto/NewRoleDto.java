package com.microservice_level_up.module.role.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Builder
public record NewRoleDto(

        @Length(max = 100, message = "name: can be max 100 characters")
        @NotBlank(message = "name: must not be null or blank")
        String name,

        @NotBlank(message = "description: must not be null or blank")
        String description
) {
}
