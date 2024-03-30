package com.microservice_level_up.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.dto.PointsResponse;
import com.microservice_level_up.enums.MovementType;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.notification.PurchaseNotification;
import common.grpc.common.ProductRequestByCode;
import common.grpc.common.ProductServiceGrpc;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public record PurchaseEmailService(
        JavaMailSender mailSender,
        ObjectMapper objectMapper,
        ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub) {

    @KafkaListener(
            topics = "purchase",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1"
    )
    public void sendEmail(Event<?> event) throws IOException, MessagingException {
        PurchaseNotification notification = objectMapper.convertValue(event.data(), PurchaseNotification.class);
        log.info("Send purchase email {}", notification);

        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipients(Message.RecipientType.TO, notification.invoice().email());
        message.setSubject("Invoice");

        String template = Files.readString(Paths.get(
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("templates/purchase_invoice.html")
                        .getPath()
        ));
        template = template
                .replace("${id}", String.valueOf(notification.invoice().id()))
                .replace("${date}", notification.invoice().datetime().toLocalDate().toString())
                .replace("${fullname}", notification.invoice().fullname())
                .replace("${email}", notification.invoice().email())
                .replace("${paymentMethodName}", notification.invoice().paymentMethod().name())
                .replace("${pointMovements}", getTableRowsForPointsMovements(notification.invoice().pointMovements()))
                .replace("${products}", getTableRowsForProducts(notification.invoice().products()))
                .replace("${subtotal}", String.valueOf(notification.invoice().subtotal()))
                .replace("${tax}", String.valueOf(notification.invoice().tax()))
                .replace("${total}", String.valueOf(notification.invoice().total()));

        message.setContent(template, "text/html; charset=utf-8");

        mailSender.send(message);
    }

    private String getTableRowsForPointsMovements (List<PointsResponse> pointMovements){
        StringBuilder tableRows = new StringBuilder();

        for (PointsResponse pointMovement : pointMovements){
            if (Objects.requireNonNull(pointMovement.type()) == MovementType.REDEMPTION) {
                tableRows.append("\n<tr class=\"details\"> <td>Redemption</td> <td class=\"text-left\">")
                        .append(pointMovement.points())
                        .append("</td> </tr>");
            } else if (pointMovement.type() == MovementType.ACCUMULATION) {
                tableRows.append("\n<tr class=\"details\"> <td>Accumulation</td> <td class=\"text-left\">")
                        .append(pointMovement.points())
                        .append("</td> </tr>");
            }
        }

        return tableRows.toString();
    }

    private String getTableRowsForProducts(List<BuyProductRequest> products) {
        StringBuilder tableRows = new StringBuilder();

        for (BuyProductRequest product : products){
            tableRows.append("\n<tr class=\"item\"> <td>")
                    .append(product.quantity())
                    .append(" of: ")
                    .append(productServiceBlockingStub
                            .getProductByCode(ProductRequestByCode.newBuilder().setCode(product.code()).build())
                            .getName())
                    .append("</td> <td class=\"text-left\">$")
                    .append(product.price())
                    .append("</td> </tr>");
        }

        return tableRows.toString();
    }
}
