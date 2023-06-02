package com.microservice_level_up.dto;

public record NotificationRequest(
        String emailTo,
        String subject,
        String message) {
}
