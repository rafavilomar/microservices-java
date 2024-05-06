package com.microservice_level_up.module.point;

import com.microservice_level_up.dto.PointsResponse;
import com.microservice_level_up.module.point.dto.PurchaseRequest;
import com.microservice_level_up.module.point.dto.SimpleLotPoints;

import java.util.Optional;

public interface IPointsService {
    PointsResponse accumulatePoints(PurchaseRequest purchaseRequest);

    PointsResponse redeemPoints(PurchaseRequest purchaseRequest);

    Optional<SimpleLotPoints> getPointsInfo(long idCustomer);
}
