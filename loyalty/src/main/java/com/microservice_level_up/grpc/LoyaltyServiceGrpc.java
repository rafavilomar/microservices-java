package com.microservice_level_up.grpc;

import com.microservice_level_up.module.point.IPointsService;
import com.microservice_level_up.module.point.dto.PurchaseRequest;
import common.grpc.common.PointsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@GrpcService
public class LoyaltyServiceGrpc extends common.grpc.common.LoyaltyServiceGrpc.LoyaltyServiceImplBase {

    private final IPointsService pointsService;

    @Override
    public void redeemPoints(common.grpc.common.PurchaseRequest request, StreamObserver<PointsResponse> responseObserver) {
        com.microservice_level_up.dto.PointsResponse response = pointsService.redeemPoints(PurchaseRequest.builder()
                .idCustomer(request.getIdCustomer())
                .dollar(request.getDollar())
                .points(request.getPoints())
                .movementDate(LocalDateTime.parse(request.getMovementDate()))
                .build());

        responseObserver.onNext(PointsResponse.newBuilder()
                .setPoints(response.points())
                .setDollar(response.points())
                .setType(response.type().toString())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void accumulatePoints(common.grpc.common.PurchaseRequest request, StreamObserver<PointsResponse> responseObserver) {
        com.microservice_level_up.dto.PointsResponse response = pointsService.accumulatePoints(PurchaseRequest.builder()
                .idCustomer(request.getIdCustomer())
                .dollar(request.getDollar())
                .points(request.getPoints())
                .movementDate(LocalDateTime.parse(request.getMovementDate()))
                .build());

        responseObserver.onNext(PointsResponse.newBuilder()
                .setPoints(response.points())
                .setDollar(response.points())
                .setType(response.type().toString())
                .build());
        responseObserver.onCompleted();
    }
}
