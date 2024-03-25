package com.microservice_level_up.module.invoice;

import com.microservice_level_up.dto.InvoiceResponse;

public interface IInvoiceService {
    void save(InvoiceResponse invoiceResponse, String uuid);
}
