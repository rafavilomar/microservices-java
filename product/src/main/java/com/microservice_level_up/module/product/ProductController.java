package com.microservice_level_up.module.product;

import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.module.product.dto.FilterProductRequest;
import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
import com.microservice_level_up.dto.ProductResponse;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Products")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final IProductService service;

    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('GET_PRODUCT')")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<BaseResponse<ProductResponse>> getById(@PathVariable("productId") long productId) {
        log.info("Get product by id {}", productId);
        ProductResponse payload = service.getById(productId);

        BaseResponse<ProductResponse> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Product found"
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GET_PRODUCT')")
    @Operation(summary = "Get all products paged")
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        log.info("Get all products");
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductResponse> payload = service.getAll(pageable);

        BaseResponse<Page<ProductResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Products found"
        );
    }

    @Operation(summary = "Create a new product")
    @PreAuthorize("hasAuthority('CREATE_PRODUCT')")
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> add(@RequestBody @Valid ProductRegistrationRequest request) {
        log.info("Add new product {}", request);
        long payload = service.add(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Product added successfully"
        );
    }

    @PostMapping("/buy")
    @PreAuthorize("hasAuthority('BUY_PRODUCT')")
    @Operation(summary = "Buy a product")
    public ResponseEntity<BaseResponse<Void>> buy(@RequestBody List<BuyProductRequest> request) {
        log.info("Buy products {}", request);
        service.buy(request);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.OK,
                ""
        );
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('GET_PRODUCT')")
    @Operation(summary = "Get products by code, name and/or category")
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> filter(@RequestBody FilterProductRequest request) {

        log.info("Filter products {}", request);
        Page<ProductResponse> payload = service.filter(request);

        BaseResponse<Page<ProductResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Products found"
        );
    }

}
