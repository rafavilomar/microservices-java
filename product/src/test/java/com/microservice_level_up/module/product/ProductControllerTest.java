package com.microservice_level_up.module.product;

import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.product.dto.ProductResponse;
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

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertNotNull(actualResponse.getBody().getPayload());
        assertEquals(expectedPayload, actualResponse.getBody().getPayload());
        assertEquals("Product found", actualResponse.getBody().getMessage());

        verify(service, times(1)).getById(productId);
        verifyNoMoreInteractions(service);
    }
}