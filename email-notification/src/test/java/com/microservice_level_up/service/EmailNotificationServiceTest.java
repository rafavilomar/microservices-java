package com.microservice_level_up.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.notification.CustomerCreatedNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
class EmailNotificationServiceTest {

    @InjectMocks
    private EmailNotificationService underTest;

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail() {
        CustomerCreatedNotification customer = new CustomerCreatedNotification("David", "Trump", "david@gmail.com");
        Event<CustomerCreatedNotification> event = new Event<>(
                "ID",
                LocalDateTime.now(),
                EventType.CREATED,
                customer);

        when(objectMapper.convertValue(event.data(), CustomerCreatedNotification.class)).thenReturn(customer);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customer.email());
        message.setSubject("Registration");
        message.setText("Welcome " + customer.firstName() + " " + customer.lastName());

        underTest.sendEmail(event);

        verify(mailSender, times(1)).send(message);
    }
}