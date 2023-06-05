package com.microservice_level_up.module.product;

import com.microservice_level_up.module.product.dto.ProductResponse;

public interface IProductService {
    ProductResponse getById(long id);
}
