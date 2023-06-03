package com.microservice_level_up.dto;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {
}
