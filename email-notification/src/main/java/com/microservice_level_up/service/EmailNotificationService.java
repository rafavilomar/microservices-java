package com.microservice_level_up.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.notification.CustomerCreatedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public record EmailNotificationService(JavaMailSender mailSender) {

    @KafkaListener(
            topics = "customer_created",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1"
    )
    public void sendEmail(Event<?> event) {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerCreatedNotification customer = objectMapper.convertValue(event.data(), CustomerCreatedNotification.class);
        log.info("Send email {}", customer);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customer.email());
        message.setSubject("Registration");
        message.setText("Welcome " + customer.firstName() + " " + customer.lastName());

        mailSender.send(message);
    }
}
