package com.microservice_level_up.module.role.dto;

import lombok.Builder;

@Builder
public record PermissionResponseDto(
        String code,
        String description
) {
}
