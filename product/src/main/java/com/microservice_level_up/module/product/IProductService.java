package com.microservice_level_up.module.product;

import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
import com.microservice_level_up.module.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    ProductResponse getById(long id);
    Page<ProductResponse> getAll(Pageable pageable);
    long add(ProductRegistrationRequest request);
}
