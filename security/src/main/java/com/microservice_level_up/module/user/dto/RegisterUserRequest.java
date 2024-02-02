package com.microservice_level_up.module.user.dto;

import lombok.Builder;

@Builder
public record RegisterUserRequest(
        String email,
        String password,
        String roleName
) {
}
