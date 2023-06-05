package com.microservice_level_up.module.product;

import com.microservice_level_up.module.product.dto.ProductResponse;
import com.microservice_level_up.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
