package com.microservice_level_up.module.product;

import com.microservice_level_up.module.category.Category;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void getById() {
        long productId = 1L;
        Product product = productTemplate(productId);

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(productId)
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(CategoryResponse.builder()
                        .id(1L)
                        .name("Test")
                        .description("Category test")
                        .build())
                .build();

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
}