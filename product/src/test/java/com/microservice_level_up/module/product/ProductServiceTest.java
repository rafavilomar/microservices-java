package com.microservice_level_up.module.product;

import com.microservice_level_up.error.not_enough_points.DuplicatedProductCodeException;
import com.microservice_level_up.module.category.Category;
import com.microservice_level_up.module.category.ICategoryService;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.product.dto.BuyProductRequest;
import com.microservice_level_up.module.product.dto.FilterProductRequest;
import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
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

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;
    @Mock
    private ICategoryService categoryService;

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

    private Product productTemplateWithoutId() {
        return Product.builder()
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

    private ProductResponse responseTemplate(long productId) {
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

    private ProductRegistrationRequest registrationTemplate() {
        return ProductRegistrationRequest.builder()
                .code("test")
                .name("Product test")
                .price(10)
                .stock(1)
                .categoryId(1L)
                .build();
    }

    @Test
    void getById() {
        long productId = 1L;
        Product product = productTemplate(productId);

        ProductResponse expectedResponse = responseTemplate(productId);

        when(repository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = service.getById(productId);

        assertEquals(expectedResponse, response);

        verify(repository, times(1)).findById(productId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(categoryService);
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
        verifyNoInteractions(categoryService);
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
                responseTemplate(1L),
                responseTemplate(2L)
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

    @Test
    void add() {
        long productId = 1L;
        ProductRegistrationRequest request = registrationTemplate();
        Product product = productTemplate(productId);

        when(repository.findByCode(request.code())).thenReturn(Optional.empty());
        when(categoryService.getById(request.categoryId())).thenReturn(product.getCategory());

        System.out.println(product);

        when(repository.save(any(Product.class))).thenReturn(product);

        long actualResponse = service.add(request);
        assertEquals(productId, actualResponse);

        verify(repository, times(1)).findByCode(request.code());
        verify(categoryService, times(1)).getById(request.categoryId());
        verify(repository, times(1)).save(any(Product.class));
        verifyNoMoreInteractions(repository, categoryService);
    }

    @Test
    void addInvalidCategory() {
        ProductRegistrationRequest request = registrationTemplate();

        when(repository.findByCode(request.code())).thenReturn(Optional.empty());
        when(categoryService.getById(request.categoryId()))
                .thenThrow(new EntityNotFoundException("Category not found for this id: " + request.categoryId()));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.add(request)
        );

        assertEquals("Category not found for this id: " + request.categoryId(), exception.getMessage());

        verify(repository, times(1)).findByCode(request.code());
        verify(categoryService, times(1)).getById(request.categoryId());
        verifyNoMoreInteractions(repository, categoryService);
    }

    @Test
    void addDuplicatedCode() {
        ProductRegistrationRequest request = registrationTemplate();
        Product product = productTemplateWithoutId();
        when(repository.findByCode(request.code())).thenReturn(Optional.of(product));

        DuplicatedProductCodeException exception = assertThrows(
                DuplicatedProductCodeException.class,
                () -> service.add(request)
        );

        assertEquals("Duplicated code: " + request.code(), exception.getMessage());

        verify(repository, times(1)).findByCode(request.code());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(categoryService);
    }

    @Test
    void buy_ShouldBeOk() {
        List<BuyProductRequest> buyProducts = List.of(new BuyProductRequest(1, "test"));
        List<Product> products = List.of(productTemplate(1L));

        when(repository.findByCodeIn(buyProducts.stream().map(BuyProductRequest::code).toList())).thenReturn(products);

        service.buy(buyProducts);

        products = products.stream()
                .peek(product -> product.setStock(product.getStock() - 1))
                .toList();
        List<String> codes = buyProducts.stream()
                .map(BuyProductRequest::code)
                .collect(Collectors.toList());

        verify(repository, times(1)).findByCodeIn(codes);
        verify(repository, times(1)).saveAll(products);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(categoryService);
    }

    @Test
    void buy_NotFound() {
        List<BuyProductRequest> buyProducts = List.of(new BuyProductRequest(1, "test"));

        when(repository.findByCodeIn(buyProducts.stream().map(BuyProductRequest::code).toList()))
                .thenReturn(new ArrayList<>());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.buy(buyProducts));

        List<String> codes = buyProducts.stream().map(BuyProductRequest::code).toList();

        assertEquals("Products " + codes + " do not exist", exception.getMessage());

        verify(repository, times(1)).findByCodeIn(codes);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(categoryService);
    }

    @Test
    void filter_ShouldBeOk() {
        FilterProductRequest request = FilterProductRequest.builder()
                .productCode("test")
                .productName("Product test")
                .categoryName("category")
                .page(1)
                .size(1)
                .build();
        Pageable pageable = PageRequest.of(request.page() - 1, request.size());

        List<Product> productList = List.of(productTemplate(1L));
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(repository.findAllByNameOrCodeOrCategory_Name(
                request.productName(),
                request.productCode(),
                request.categoryName(),
                pageable))
                .thenReturn(productPage);

        Page<ProductResponse> actualResponse = service.filter(request);

        assertAll(
                "Filter products",
                () -> assertTrue(actualResponse.isLast()),
                () -> assertTrue(actualResponse.isFirst()),
                () -> assertEquals(1, actualResponse.getContent().size()),
                () -> assertEquals(request.productCode(), actualResponse.getContent().get(0).code())
        );

        verify(repository, times(1)).findAllByNameOrCodeOrCategory_Name(
                request.productName(),
                request.productCode(),
                request.categoryName(),
                pageable);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(categoryService);
    }
}