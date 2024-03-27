package com.microservice_level_up.notification;

import com.microservice_level_up.dto.InvoiceResponse;

public record PurchaseNotification(
        InvoiceResponse invoice
) {
}
