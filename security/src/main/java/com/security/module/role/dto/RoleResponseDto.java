package com.security.module.role.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RoleResponseDto(
        long id,
        String name,
        String description,
        boolean status,
        List<PermissionResponseDto> permissions
) {
}
