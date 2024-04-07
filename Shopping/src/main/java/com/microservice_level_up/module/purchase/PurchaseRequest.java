package com.microservice_level_up.module.purchase;

import com.microservice_level_up.dto.BuyProductRequest;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PurchaseRequest(
        long idCustomer,
        long idPaymentMethod,
        int pointsRedemption,
        List<BuyProductRequest> products,
        double subtotal,
        double tax,
        double total,
        LocalDateTime datetime
) {
}
