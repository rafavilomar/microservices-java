package com.microservice_level_up.module.purchase;

import com.microservice_level_up.dto.InvoiceResponse;

public interface IPurchaseService {
    InvoiceResponse purchase(PurchaseRequest request);
}
