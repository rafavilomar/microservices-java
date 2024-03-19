package com.microservice_level_up.service;

import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.dto.PurchaseRequest;

public interface IPurchaseService {
    InvoiceResponse purchase(PurchaseRequest request);

    void getAllPurchase();

    void getPurchaseById(long id);
}
