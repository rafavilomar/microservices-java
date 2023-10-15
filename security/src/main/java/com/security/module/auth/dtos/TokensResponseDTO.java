package com.security.module.auth.dtos;

import lombok.Builder;

@Builder
public record TokensResponseDTO(
        String accessToken,
        String refreshToken
) {
}
