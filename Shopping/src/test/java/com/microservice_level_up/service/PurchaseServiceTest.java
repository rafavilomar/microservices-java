package com.microservice_level_up.service;

import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.dto.PurchaseRequest;
import com.microservice_level_up.enums.MovementType;
import common.grpc.common.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseServiceTest {

    @InjectMocks
    private PurchaseService underTest;

    @Mock
    private CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub;
    @Mock
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    @Mock
    private LoyaltyServiceGrpc.LoyaltyServiceBlockingStub loyaltyServiceBlockingStub;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void purchase() {
        PurchaseRequest request = PurchaseRequest.builder()
                .idCustomer(1)
                .pointsRedemption(10)
                .products(List.of(new com.microservice_level_up.dto.BuyProductRequest(2, "CODE", 16)))
                .subtotal(30)
                .tax(3)
                .total(33)
                .datetime(LocalDateTime.now())
                .build();
        CustomerResponse customer = CustomerResponse.newBuilder()
                .setId(request.idCustomer())
                .setFirstName("Donald")
                .setLastName("Trump")
                .setEmail("email@gmail.com")
                .build();
        InvoiceResponse expectedResponse = InvoiceResponse.builder()
                .fullname(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .products(request.products())
                .pointMovements(List.of(
                        new com.microservice_level_up.dto.PointsResponse(10, 2, MovementType.REDEMPTION),
                        new com.microservice_level_up.dto.PointsResponse(30, 30, MovementType.ACCUMULATION)
                ))
                .subtotal(request.subtotal())
                .tax(request.tax())
                .total(request.total())
                .datetime(request.datetime())
                .build();

        when(customerServiceBlockingStub.getById(CustomerRequest.newBuilder().setId(request.idCustomer()).build()))
                .thenReturn(customer);
        when(loyaltyServiceBlockingStub.redeemPoints(common.grpc.common.PurchaseRequest.newBuilder()
                .setIdCustomer(request.idCustomer())
                .setPoints(request.pointsRedemption())
                .setMovementDate(request.datetime().toString())
                .build())).thenReturn(PointsResponse.newBuilder()
                .setPoints(10)
                .setDollar(2)
                .setType(MovementType.REDEMPTION.toString())
                .build());
        when(loyaltyServiceBlockingStub.accumulatePoints(common.grpc.common.PurchaseRequest.newBuilder()
                .setIdCustomer(request.idCustomer())
                .setDollar(request.total())
                .setMovementDate(request.datetime().toString())
                .build())).thenReturn(PointsResponse.newBuilder()
                .setPoints(30)
                .setDollar(30)
                .setType(MovementType.ACCUMULATION.toString())
                .build());

        InvoiceResponse actualResponse = underTest.purchase(request);

        assertEquals(expectedResponse, actualResponse);

        verify(customerServiceBlockingStub, times(1)).getById(CustomerRequest.newBuilder()
                .setId(request.idCustomer())
                .build());
        verify(loyaltyServiceBlockingStub, times(1)).redeemPoints(common.grpc.common.PurchaseRequest.newBuilder()
                .setIdCustomer(request.idCustomer())
                .setPoints(request.pointsRedemption())
                .setMovementDate(request.datetime().toString())
                .build());
        verify(productServiceBlockingStub, times(1)).buy(BuyProductRequest.newBuilder()
                .addAllProducts(request.products()
                        .stream()
                        .map(product -> Product.newBuilder().setCode(product.code()).setQuantity(product.quantity()).build())
                        .toList())
                .build());
        verify(loyaltyServiceBlockingStub, times(1)).accumulatePoints(common.grpc.common.PurchaseRequest.newBuilder()
                        .setIdCustomer(request.idCustomer())
                        .setDollar(request.total())
                        .setMovementDate(request.datetime().toString())
                        .build());
        verifyNoMoreInteractions(customerServiceBlockingStub, loyaltyServiceBlockingStub, productServiceBlockingStub);
    }
}