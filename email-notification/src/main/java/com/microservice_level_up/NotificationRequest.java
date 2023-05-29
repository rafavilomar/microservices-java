package com.microservice_level_up;

public record NotificationRequest(
        String emailTo,
        String subject,
        String message) {
}
