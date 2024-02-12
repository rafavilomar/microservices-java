package com.microservice_level_up.module.auth.dtos;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String email,
        String roleName
) {
}
