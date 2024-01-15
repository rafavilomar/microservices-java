package com.microservice_level_up.module.payment_method.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
