package com.microservice_level_up.module.point.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PurchaseRequest(
        long idCustomer,
        double dollar,
        int points,
        LocalDateTime movementDate,
        String invoiceUuid
) {
}
