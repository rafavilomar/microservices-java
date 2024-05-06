package com.microservice_level_up.module.customer.dto;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String country,
        String address) {
}
