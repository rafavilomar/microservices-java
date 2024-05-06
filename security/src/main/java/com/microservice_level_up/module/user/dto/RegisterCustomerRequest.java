package com.microservice_level_up.module.user.dto;

import lombok.Builder;

@Builder
public record RegisterCustomerRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String country,
        String address
) {
}
