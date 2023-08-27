package com.microservice_level_up.module.user.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        long id,
        String email,
        String fullName
) {
}
