package com.customer.module.payment_method.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record PaymentMethodRegistration(
        @NotNull(message = "customerId: must not be null")
        @Positive(message = "customerId: must be higher than zero")
        long customerId,

        @NotBlank(message = "methodName: must not be null or blank")
        @Size(max = 64, message = "methodName: must be max 64 characters")
        String methodName,

        @NotBlank(message = "cardNumber: must not be null or blank")
        @Size(min = 16, max = 16, message = "cardNumber: must be 16 digits")
        String cardNumber,

        @NotBlank(message = "alias: must not be null or blank")
        @Size(max = 64, message = "alias: must be max 64 characters")
        String alias,

        @NotNull(message = "expirationMonth: must not be null")
        @Range(min = 1, max = 12, message = "expirationMonth: must be a valid month")
        int expirationMonth,

        @NotNull(message = "expirationYear: must not be null")
        int expirationYear,

        @NotNull(message = "cvv: must not be null")
        @Range(min = 100, max = 999, message = "cvv: must be between 100 and 999")
        int cvv
) {
}
