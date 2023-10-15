package com.security.module.role.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Builder
public record RoleAndPermissionDto(

        @NotNull(message = "idRole: must not be null")
        @Positive(message = "idRole: must be higher than 0")
        long idRole,

        @Length(max = 30, message = "permissionCode: can be max 30 characters")
        @NotBlank(message = "permissionCode: must not be null or blank")
        String permissionCode
) {
}
