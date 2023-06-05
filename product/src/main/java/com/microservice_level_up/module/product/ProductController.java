package com.microservice_level_up.module.product;

import com.microservice_level_up.module.product.dto.ProductResponse;
import com.microservice_level_up.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/product")
public record ProductController(IProductService service) {

    @GetMapping("/{productId}")
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

}
