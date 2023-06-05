package com.microservice_level_up.module.product;

import com.microservice_level_up.module.category.Category;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Product productTemplate(long productId) {
        return Product.builder()
                .id(productId)
                .code("test")
                .name("Product test")
                .price(10)
                .stock(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .category(Category.builder()
                        .id(1L)
                        .name("Test")
                        .description("Category test")
                        .build())
                .build();
    }

    private ProductResponse productResponseTemplate(long productId) {
        return ProductResponse.builder()
                .id(productId)
                .code("test")
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
        Product product = productTemplate(productId);

        ProductResponse expectedResponse = productResponseTemplate(productId);

        when(repository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = service.getById(productId);

        assertEquals(expectedResponse, response);

        verify(repository, times(1)).findById(productId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getByIdNotFound() {
        long productId = 1L;
        when(repository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.getById(productId)
        );

        assertEquals("Product not found for this id: " + productId, exception.getMessage());

        verify(repository, times(1)).findById(productId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAll() {

        Pageable pageable = PageRequest.of(0, 1);

        List<Product> productList = List.of(
                productTemplate(1L),
                productTemplate(2L)
        );
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(repository.findAll(pageable)).thenReturn(productPage);

        List<ProductResponse> productResponseList = List.of(
                productResponseTemplate(1L),
                productResponseTemplate(2L)
        );
        Page<ProductResponse> expectedResponse = new PageImpl<>(productResponseList, pageable, productResponseList.size());

        Page<ProductResponse> actualResponse = service.getAll(pageable);

        assertNotNull(actualResponse);
        assertAll(
                "Product get all service",
                () -> assertEquals(expectedResponse, actualResponse),
                () -> assertFalse(actualResponse.isLast()),
                () -> assertTrue(actualResponse.isFirst()),
                () -> assertEquals(2L, actualResponse.getTotalElements()),
                () -> assertEquals(1, actualResponse.getSize())
        );

        verify(repository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(repository);
    }

}