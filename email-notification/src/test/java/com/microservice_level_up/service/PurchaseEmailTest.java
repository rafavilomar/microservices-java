package com.microservice_level_up.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.notification.PurchaseNotification;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class PurchaseEmailTest {

    @InjectMocks
    private PurchaseEmailService underTest;

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled
    void sendEmail() throws MessagingException, IOException {
        PurchaseNotification notification = new PurchaseNotification(InvoiceResponse.builder().email("david@gmail.com").build());
        Event<PurchaseNotification> event = new Event<>(
                "ID",
                LocalDateTime.now(),
                EventType.CREATED,
                notification);

        when(objectMapper.convertValue(event.data(), PurchaseNotification.class)).thenReturn(notification);

        underTest.sendEmail(event);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verifyNoMoreInteractions(mailSender);
    }
}