package com.microservice_level_up.module.product;

import com.microservice_level_up.dto.CategoryResponse;
import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
import com.microservice_level_up.dto.ProductResponse;
import com.microservice_level_up.response.BaseResponse;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @InjectMocks
    private ProductController controller;

    @Mock
    private IProductService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ProductResponse expectedPayloadTemplate(long productId) {
        return ProductResponse.builder()
                .id(productId)
                .code("Test")
                .name("Product test")
                .price(10)
                .stock(1)
                .category(CategoryResponse.builder()
                        .id(1L)
                        .name("Test")
                        .description("Category test")
                        .build())
                .build();
    }

    @Test
    void getById() {
        long productId = 1L;
        ProductResponse expectedPayload = expectedPayloadTemplate(productId);

        when(service.getById(productId)).thenReturn(expectedPayload);

        ResponseEntity<BaseResponse<ProductResponse>> actualResponse = controller.getById(productId);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Product get by id controller response",
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()),
                () -> assertNotNull(actualResponse.getBody().getPayload()),
                () -> assertEquals(expectedPayload, actualResponse.getBody().getPayload()),
                () -> assertEquals("Product found", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).getById(productId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getAll() {
        int page = 1;
        int size = 1;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<ProductResponse> productResponseList = List.of(
                expectedPayloadTemplate(1L),
                expectedPayloadTemplate(2L)
        );
        Page<ProductResponse> expectedPayload = new PageImpl<>(
                productResponseList,
                pageable,
                productResponseList.size());

        when(service.getAll(pageable)).thenReturn(expectedPayload);

        ResponseEntity<BaseResponse<Page<ProductResponse>>> actualResponse = controller.getAll(page, size);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Product get all controller response",
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()),
                () -> assertNotNull(actualResponse.getBody().getPayload()),
                () -> assertEquals(expectedPayload, actualResponse.getBody().getPayload()),
                () -> assertEquals("Products found", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).getAll(pageable);
        verifyNoMoreInteractions(service);
    }

    @Test
    void add() {
        long productId = 1L;
        ProductRegistrationRequest request = ProductRegistrationRequest.builder()
                .name("Product test")
                .code("Test")
                .categoryId(20L)
                .stock(20)
                .price(15)
                .build();

        when(service.add(request)).thenReturn(productId);

        ResponseEntity<BaseResponse<Long>> actualResponse = controller.add(request);

        assertNotNull(actualResponse.getBody());
        assertAll(
                "Product add controller response",
                () -> assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode()),
                () -> assertNotNull(actualResponse.getBody().getPayload()),
                () -> assertEquals(productId, actualResponse.getBody().getPayload()),
                () -> assertEquals("Product added successfully", actualResponse.getBody().getMessage())
        );

        verify(service, times(1)).add(request);
        verifyNoMoreInteractions(service);
    }
}