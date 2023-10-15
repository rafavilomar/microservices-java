package com.security.module.user.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        long id,
        String email,
        String fullName,
        long idRole,
        String roleName
) {
}
