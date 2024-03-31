package com.microservice_level_up.module.invoice;

import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.dto.InvoicePaymentMethod;
import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.module.invoice.entity.Invoice;
import com.microservice_level_up.module.invoice.entity.Product;
import com.microservice_level_up.module.invoice.enums.InvoiceType;
import com.microservice_level_up.module.invoice.repository.InvoiceRepository;
import com.microservice_level_up.module.invoice.repository.ProductRepository;
import com.microservice_level_up.module.purchase.PurchaseRequest;
import common.grpc.common.CustomerResponse;
import common.grpc.common.CustomerServiceGrpc;
import common.grpc.common.PaymentMethodRequest;
import common.grpc.common.PaymentMethodResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService underTest;

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save(){
        UUID uuid = UUID.randomUUID();
        CustomerResponse customer = CustomerResponse.newBuilder()
                .setFirstName("Saul")
                .setLastName("Goodman")
                .setEmail("saul@gmail.com")
                .build();
        PaymentMethodResponse paymentMethod = PaymentMethodResponse.newBuilder()
                .setId(1)
                .setMethodName("Credit card")
                .build();
        PaymentMethodRequest paymentMethodRequest = PaymentMethodRequest.newBuilder()
                .setId(paymentMethod.getId())
                .build();
        PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                .products(List.of(new BuyProductRequest(2, "CODE", 15)))
                .idPaymentMethod(paymentMethod.getId())
                .subtotal(30)
                .tax(3)
                .total(33)
                .datetime(LocalDateTime.now())
                .build();

        Invoice invoice = Invoice.builder()
                .id(1L)
                .uuid(uuid.toString())
                .fullname(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .type(InvoiceType.PURCHASE)
                .subtotal(purchaseRequest.subtotal())
                .tax(purchaseRequest.tax())
                .total(purchaseRequest.total())
                .datetime(purchaseRequest.datetime())
                .build();

        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(customerServiceBlockingStub.getPaymentMethodById(paymentMethodRequest)).thenReturn(paymentMethod);

        InvoiceResponse actualResponse = underTest.save(customer, uuid.toString(), purchaseRequest, new ArrayList<>());
        InvoiceResponse expectedResponse = InvoiceResponse.builder()
                .id(invoice.getId())
                .fullname(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .paymentMethod(new InvoicePaymentMethod(paymentMethod.getId(), paymentMethod.getMethodName()))
                .products(purchaseRequest.products())
                .pointMovements(new ArrayList<>())
                .subtotal(purchaseRequest.subtotal())
                .tax(purchaseRequest.tax())
                .total(purchaseRequest.total())
                .datetime(purchaseRequest.datetime())
                .build();

        assertEquals(expectedResponse, actualResponse);

        verify(invoiceRepository, times(1)).save(any());
        verify(productRepository, times(1)).saveAll(List.of(
                Product.builder()
                        .invoice(invoice)
                        .code(expectedResponse.products().get(0).code())
                        .price(expectedResponse.products().get(0).price())
                        .quantity(expectedResponse.products().get(0).quantity())
                        .build()
        ));
        verify(customerServiceBlockingStub, times(1)).getPaymentMethodById(paymentMethodRequest);
        verifyNoMoreInteractions(invoiceRepository, productRepository, customerServiceBlockingStub);
    }
}