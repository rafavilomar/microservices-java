package com.microservice_level_up.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InvoiceResponse(
        long id,
        String fullname,
        String email,
        InvoicePaymentMethod paymentMethod,
        List<BuyProductRequest> products,
        List<PointsResponse> pointMovements,
        double subtotal,
        double tax,
        double total,
        LocalDateTime datetime
) {
}
