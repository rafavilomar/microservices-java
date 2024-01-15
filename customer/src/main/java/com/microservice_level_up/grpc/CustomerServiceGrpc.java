package com.microservice_level_up.grpc;

import com.google.protobuf.Empty;
import com.microservice_level_up.module.customer.ICustomerService;
import common.grpc.common.CustomerRegistrationRequest;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
@RequiredArgsConstructor
public class CustomerServiceGrpc extends common.grpc.common.CustomerServiceGrpc.CustomerServiceImplBase {

    private final ICustomerService customerService;

    @Override
    public void register(CustomerRegistrationRequest request, StreamObserver<Empty> responseObserver) {
        customerService.register(com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .country(request.getCountry())
                .idUser(request.getIdUser())
                .build());

        responseObserver.onCompleted();
    }
}
