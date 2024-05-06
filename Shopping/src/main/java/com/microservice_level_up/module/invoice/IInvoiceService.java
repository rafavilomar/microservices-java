package com.microservice_level_up.module.invoice;

import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.dto.PointsResponse;
import com.microservice_level_up.module.purchase.PurchaseRequest;
import common.grpc.common.CustomerResponse;

import java.util.List;

public interface IInvoiceService {
    InvoiceResponse save(
            CustomerResponse customer,
            String uuid,
            PurchaseRequest purchaseRequest,
            List<PointsResponse> pointsMovements);
}
