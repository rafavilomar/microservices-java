package com.customer.module.payment_method;

import com.customer.module.payment_method.dto.PaymentMethodRegistration;
import com.customer.module.payment_method.dto.PaymentMethodResponse;
import com.customer.response.BaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Month;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentMethodControllerTest {
    @InjectMocks
    private PaymentMethodController controller;

    @Mock
    private IPaymentMethodService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add() {
        PaymentMethodRegistration request = new PaymentMethodRegistration(
                1L,
                "Payment test",
                "12345",
                "David Peterson",
                Month.APRIL.getValue(),
                Year.now().getValue(),
                100
        );

        long expectedResponse = 20L;
        when(service.add(request)).thenReturn(expectedResponse);
        ResponseEntity<BaseResponse<Long>> actualResponse = controller.add(request);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Payment method add controller response",
                () -> assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode()),
                () -> assertNotNull(actualResponse.getBody().getPayload()),
                () -> assertEquals(expectedResponse, actualResponse.getBody().getPayload()),
                () -> assertEquals("Payment method registered", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).add(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getById() {
        long id = 1L;
        PaymentMethodResponse expectedPaymentMethod = new PaymentMethodResponse(
                id,
                20L,
                "Payment test",
                "12345",
                "David Peterson",
                Month.APRIL.getValue(),
                Year.now().getValue(),
                100
        );

        when(service.getById(id)).thenReturn(expectedPaymentMethod);

        ResponseEntity<BaseResponse<PaymentMethodResponse>> actualResponse = controller.getById(id);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Payment method get by id controller response",
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()),
                () -> assertNotNull(actualResponse.getBody().getPayload()),
                () -> assertEquals(expectedPaymentMethod, actualResponse.getBody().getPayload()),
                () -> assertEquals("Payment method found", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).getById(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getByCustomerId() {
        long customerId = 20L;
        int page = 1;
        int size = 2;

        List<PaymentMethodResponse> paymentMethodResponseList = List.of(
                buildPaymentMethodResponse(1L, customerId),
                buildPaymentMethodResponse(2L, customerId)
        );

        Pageable pageable = PageRequest.of(page - 1, size);
        PageImpl<PaymentMethodResponse> expectedResponse = new PageImpl<>(
                paymentMethodResponseList,
                pageable,
                paymentMethodResponseList.size()
        );

        when(service.getByCustomerId(customerId, pageable)).thenReturn(expectedResponse);

        ResponseEntity<BaseResponse<Page<PaymentMethodResponse>>> actualResponse = controller.getByCustomerId(customerId, page, size);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Payment method get by customer's id controller response",
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()),
                () -> assertNotNull(actualResponse.getBody().getPayload()),
                () -> assertEquals(expectedResponse, actualResponse.getBody().getPayload()),
                () -> assertEquals("Payment methods found for this customer", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).getByCustomerId(customerId, pageable);
        verifyNoMoreInteractions(service);
    }

    private PaymentMethodResponse buildPaymentMethodResponse(long order, long customerId) {
        return new PaymentMethodResponse(
                order,
                customerId,
                "Payment test",
                "12345",
                "David Peterson",
                Month.APRIL.getValue(),
                Year.now().getValue(),
                100
        );
    }

    @Test
    void remove() {
        long id = 1L;
        ResponseEntity<BaseResponse<Void>> actualResponse = controller.remove(id);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Payment method remove controller response",
                () -> assertEquals(HttpStatus.NO_CONTENT, actualResponse.getStatusCode()),
                () -> assertNull(actualResponse.getBody().getPayload()),
                () -> assertEquals("Payment method removed", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).remove(id);
        verifyNoMoreInteractions(service);
    }
}