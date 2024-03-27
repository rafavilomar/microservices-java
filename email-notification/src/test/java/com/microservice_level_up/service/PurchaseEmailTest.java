package com.microservice_level_up.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.notification.PurchaseNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class PurchaseEmailTest {

    @InjectMocks
    private PurchaseEmail underTest;

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
        PurchaseNotification notification = new PurchaseNotification(InvoiceResponse.builder().email("david@gmail.com").build());
        Event<PurchaseNotification> event = new Event<>(
                "ID",
                LocalDateTime.now(),
                EventType.CREATED,
                notification);

        when(objectMapper.convertValue(event.data(), PurchaseNotification.class)).thenReturn(notification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.invoice().email());
        message.setSubject("Invoice");
        message.setText("Purchase's invoice");

        underTest.sendEmail(event);

        verify(mailSender, times(1)).send(message);
    }
}