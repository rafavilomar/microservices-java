package com.customer.module.payment_method.dto;

public record PaymentMethodResponse(
        long id,
        long customerId,
        String methodName,
        String cardNumber,
        String alias,
        int expirationMonth,
        int expirationYear,
        int cvv
) {
}
