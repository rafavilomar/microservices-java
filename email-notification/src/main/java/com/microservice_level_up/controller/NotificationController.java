package com.microservice_level_up.controller;

import com.microservice_level_up.service.EmailNotificationService;
import com.microservice_level_up.dto.NotificationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public record NotificationController(EmailNotificationService service) {

    @PostMapping
    public void postNotification(@RequestBody NotificationRequest request) {
        service.sendEmail(request);
    }
}
