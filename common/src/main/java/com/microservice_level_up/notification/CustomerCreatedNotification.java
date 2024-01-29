package com.microservice_level_up.notification;

public record CustomerCreatedNotification(
        String firstName,
        String lastName,
        String email) {
}
