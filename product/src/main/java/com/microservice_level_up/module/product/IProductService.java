package com.microservice_level_up.module.product;

import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.module.product.dto.FilterProductRequest;
import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
import com.microservice_level_up.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    ProductResponse getById(long id);

    Page<ProductResponse> getAll(Pageable pageable);

    long add(ProductRegistrationRequest request);

    void buy(List<BuyProductRequest> buyProducts);

    Page<ProductResponse> filter(FilterProductRequest request);

    ProductResponse getByCode(String code);
}
