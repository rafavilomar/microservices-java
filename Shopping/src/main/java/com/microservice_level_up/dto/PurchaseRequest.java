package com.microservice_level_up.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PurchaseRequest(
        long idCustomer,
        int pointsRedemption,
        List<BuyProductRequest> products,
        double subtotal,
        double tax,
        double total,
        LocalDateTime datetime
) {
}
