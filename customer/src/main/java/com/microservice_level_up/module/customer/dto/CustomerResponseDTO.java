package com.microservice_level_up.module.customer.dto;

public record CustomerResponseDTO(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String country) {
}
