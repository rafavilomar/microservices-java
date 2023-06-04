package com.microservice_level_up.module.payment_method.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Month;
import java.time.Year;

public record PaymentMethodRegistration(
        @NotNull(message = "customerId: must not be null")
        @Positive(message = "customerId: must be higher than zero")
        long customerId,

        @NotBlank(message = "methodName: must not be null or blank")
        @Size(max = 64, message = "methodName: must be max 64 characters")
        String methodName,

        @NotBlank(message = "methodName: must not be null or blank")
        @Size(min = 16, max = 16, message = "methodName: must be 16 digits")
        String cardNumber,

        @NotBlank(message = "methodName: must not be null or blank")
        @Size(max = 64, message = "methodName: must be max 64 characters")
        String alias,

        @NotNull(message = "expirationMonth: must not be null")
        Month expirationMonth,

        @NotNull(message = "expirationYear: must not be null")
        Year expirationYear,

        @NotNull(message = "cvv: must not be null")
        @Range(min = 100, max = 999, message = "cvv: must be between 100 and 999")
        int cvv
) {
}
