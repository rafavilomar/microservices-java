package com.microservice_level_up.module.point.dto;

import java.time.LocalDateTime;

public record PurchaseRequest(
        long idCustomer,
        double dollar,
        int points,
        LocalDateTime movementDate
) {
}
