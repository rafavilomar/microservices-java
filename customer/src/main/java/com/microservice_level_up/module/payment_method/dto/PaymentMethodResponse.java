package com.microservice_level_up.module.payment_method.dto;

import java.time.Month;
import java.time.Year;

public record PaymentMethodResponse(
        long id,
        long customerId,
        String methodName,
        String cardNumber,
        String alias,
        Month expirationMonth,
        Year expirationYear,
        int cvv
) {
}
