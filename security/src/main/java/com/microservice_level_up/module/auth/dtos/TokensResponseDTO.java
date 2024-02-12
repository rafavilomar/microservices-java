package com.microservice_level_up.module.auth.dtos;

import lombok.Builder;

@Builder
public record TokensResponseDTO(
        String accessToken,
        String refreshToken
) {
}
