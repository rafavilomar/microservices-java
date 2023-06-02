package com.microservice_level_up;

import com.microservice_level_up.kafka.events.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public record EmailNotificationService(JavaMailSender mailSender) {


    public void sendEmail(NotificationRequest request) {
        log.info("Send email {}", request);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.emailTo());
        message.setSubject(request.subject());
        message.setText(request.message());

        mailSender.send(message);
    }

    @KafkaListener(
            topics = "customers",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1"
    )
    public void consumer(Event<?> event) {
        log.info("Info from kafka {}", event);
    }
}
