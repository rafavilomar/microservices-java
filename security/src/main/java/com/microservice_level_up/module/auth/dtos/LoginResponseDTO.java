package com.microservice_level_up.module.auth.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String email,
        String roleName,
        List<String> permissions
) {
}
