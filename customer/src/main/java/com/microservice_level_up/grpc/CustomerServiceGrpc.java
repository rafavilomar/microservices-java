package com.microservice_level_up.grpc;

import com.google.protobuf.Empty;
import com.microservice_level_up.module.customer.ICustomerService;
import com.microservice_level_up.module.customer.dto.CustomerResponse;
import common.grpc.common.CustomerRegistrationRequest;
import common.grpc.common.CustomerRequest;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@GrpcService
public class CustomerServiceGrpc extends common.grpc.common.CustomerServiceGrpc.CustomerServiceImplBase {

    private final ICustomerService customerService;

    @Override
    public void registerCustomer(CustomerRegistrationRequest request, StreamObserver<Empty> responseObserver) {
        customerService.register(com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .country(request.getCountry())
                .idUser(request.getIdUser())
                .build());

        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }

    @Override
    public void getCustomerById(CustomerRequest request, StreamObserver<common.grpc.common.CustomerResponse> responseObserver) {
        CustomerResponse customer = customerService.getById(request.getId());
        responseObserver.onNext(common.grpc.common.CustomerResponse.newBuilder()
                .setId(customer.id())
                .setFirstName(customer.firstName())
                .setLastName(customer.lastName())
                .setEmail(customer.email())
                .build());
        responseObserver.onCompleted();
    }
}
