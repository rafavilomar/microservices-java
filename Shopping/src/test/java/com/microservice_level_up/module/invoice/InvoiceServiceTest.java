package com.microservice_level_up.module.invoice;

import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.enums.MovementType;
import com.microservice_level_up.module.invoice.entity.Invoice;
import com.microservice_level_up.module.invoice.entity.Product;
import com.microservice_level_up.module.invoice.enums.InvoiceType;
import com.microservice_level_up.module.invoice.repository.InvoiceRepository;
import com.microservice_level_up.module.invoice.repository.ProductRepository;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save(){
        UUID uuid = UUID.randomUUID();
        InvoiceResponse invoiceResponse = InvoiceResponse.builder()
                .fullname("Saul Goodman")
                .email("saul@gmail.com")
                .products(List.of(new BuyProductRequest(2, "CODE", 15)))
                .pointMovements(new ArrayList<>())
                .subtotal(30)
                .tax(33)
                .total(33)
                .datetime(LocalDateTime.now())
                .build();

        Invoice invoice = Invoice.builder()
                .uuid(uuid.toString())
                .fullname(invoiceResponse.fullname())
                .email(invoiceResponse.email())
                .type(InvoiceType.PURCHASE)
                .subtotal(invoiceResponse.subtotal())
                .tax(invoiceResponse.tax())
                .total(invoiceResponse.total())
                .datetime(invoiceResponse.datetime())
                .build();

        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        underTest.save(invoiceResponse, uuid.toString());

        verify(invoiceRepository, times(1)).save(any());
        verify(productRepository, times(1)).saveAll(List.of(
                Product.builder()
                        .invoice(invoice)
                        .code(invoiceResponse.products().get(0).code())
                        .price(invoiceResponse.products().get(0).price())
                        .quantity(invoiceResponse.products().get(0).quantity())
                        .build()
        ));
        verifyNoMoreInteractions(invoiceRepository, productRepository);
    }
}