package com.microservice_level_up.grpc;

import com.google.protobuf.Empty;
import com.microservice_level_up.module.customer.CustomerService;
import common.grpc.common.CustomerRegistrationRequest;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class CustomerServiceGrpcTest {

    @InjectMocks
    private CustomerServiceGrpc underTest;

    @Mock
    private CustomerService customerService;
    @Mock
    private StreamObserver<Empty> responseObserver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() {
        CustomerRegistrationRequest requestFromGrpc = CustomerRegistrationRequest.newBuilder()
                .setFirstName("David")
                .setLastName("Trump")
                .setEmail("david@gmail.com")
                .setAddress("Address")
                .setCountry("USA")
                .setIdUser(1)
                .build();

        underTest.register(requestFromGrpc, responseObserver);

        verify(responseObserver, times(1)).onNext(null);
        verify(responseObserver, times(1)).onCompleted();
        verify(customerService, times(1)).register(com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest.builder()
                .firstName(requestFromGrpc.getFirstName())
                .lastName(requestFromGrpc.getLastName())
                .email(requestFromGrpc.getEmail())
                .address(requestFromGrpc.getAddress())
                .country(requestFromGrpc.getCountry())
                .idUser(requestFromGrpc.getIdUser())
                .build());
        verifyNoMoreInteractions(customerService, responseObserver);
    }
}