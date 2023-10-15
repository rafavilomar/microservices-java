package com.security.module.auth.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String email,
        String fullName,
        String roleName,
        List<String> permissions
) {
}
