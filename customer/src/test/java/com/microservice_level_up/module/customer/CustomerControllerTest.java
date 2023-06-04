package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.module.customer.dto.CustomerResponse;
import com.microservice_level_up.module.customer.dto.CustomerUpdateRequest;
import com.microservice_level_up.response.BaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @InjectMocks
    private CustomerController controller;

    @Mock
    private ICustomerService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById() {
        long id = 1L;
        CustomerResponse expectedCustomer = new CustomerResponse(
                id,
                "David",
                "Peterson",
                "david@gmail.com",
                "USA"
        );

        when(service.getById(id)).thenReturn(expectedCustomer);

        ResponseEntity<BaseResponse<CustomerResponse>> actualResponse = controller.getById(id);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertNotNull(actualResponse.getBody().getPayload());
        assertEquals(expectedCustomer, actualResponse.getBody().getPayload());
        assertEquals("Customer found", actualResponse.getBody().getMessage());

        verify(service, times(1)).getById(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void registerCustomer() {
        long customerId = 1L;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "David",
                "Peterson",
                "david@gmail.com",
                "USA"
        );

        when(service.register(request)).thenReturn(customerId);

        ResponseEntity<BaseResponse<Long>> actualResponse = controller.registerCustomer(request);

        assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertNotNull(actualResponse.getBody().getPayload());
        assertEquals(customerId, actualResponse.getBody().getPayload());
        assertEquals("Customer registered successfully", actualResponse.getBody().getMessage());

        verify(service, times(1)).register(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void updateCustomer() {
        long customerId = 1L;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customerId,
                "David",
                "Peterson",
                "david@gmail.com",
                "USA");

        when(service.update(request)).thenReturn(customerId);

        ResponseEntity<BaseResponse<Long>> actualResponse = controller.updateCustomer(request);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertNotNull(actualResponse.getBody().getPayload());
        assertEquals(customerId, actualResponse.getBody().getPayload());
        assertEquals("Customer updated successfully", actualResponse.getBody().getMessage());

        verify(service, times(1)).update(request);
        verifyNoMoreInteractions(service);
    }
}