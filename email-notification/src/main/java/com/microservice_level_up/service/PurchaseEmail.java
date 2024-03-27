package com.microservice_level_up.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.notification.PurchaseNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public record PurchaseEmail (JavaMailSender mailSender, ObjectMapper objectMapper) {

    @KafkaListener(
            topics = "purchase",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1"
    )
    public void sendEmail(Event<?> event) {
        PurchaseNotification purchase = objectMapper.convertValue(event.data(), PurchaseNotification.class);
        log.info("Send purchase email {}", purchase);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(purchase.invoice().email());
        message.setSubject("Invoice");
        message.setText("Purchase's invoice");

        mailSender.send(message);
    }
}
