package com.microservice_level_up.module.product;

import com.microservice_level_up.module.product.dto.ProductResponse;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public record ProductService(ProductRepository repository) implements IProductService {
    @Override
    public ProductResponse getById(long id) {
        return repository
                .findById(id)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found for this id: " + id));
    }

    private ProductResponse mapResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
