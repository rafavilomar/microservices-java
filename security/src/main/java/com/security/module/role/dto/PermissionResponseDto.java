package com.security.module.role.dto;

import lombok.Builder;

@Builder
public record PermissionResponseDto(
        String code,
        String description
) {
}
