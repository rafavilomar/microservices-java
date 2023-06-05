package com.microservice_level_up.module.product;

import com.microservice_level_up.module.category.Category;
import com.microservice_level_up.module.category.ICategoryService;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
import com.microservice_level_up.module.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public record ProductService(
        ProductRepository repository,
        ICategoryService categoryService) implements IProductService {

    @Override
    public ProductResponse getById(long id) {
        return repository
                .findById(id)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found for this id: " + id));
    }

    @Override
    public Page<ProductResponse> getAll(Pageable pageable) {
        return repository
                .findAll(pageable)
                .map(this::mapResponse);
    }

    @Override
    public long add(ProductRegistrationRequest request) {
        repository.findByCode(request.code())
                .ifPresent(product -> {
                    throw new DuplicatedProductCodeException("Duplicated code: " + request.code());
                });

        Category category = categoryService.getById(request.categoryId());

        Product product = Product.builder()
                .code(request.code())
                .price(request.price())
                .name(request.name())
                .stock(request.stock())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .category(category)
                .build();

        return repository.save(product).getId();
    }

    private ProductResponse mapResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(CategoryResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .description(product.getCategory().getDescription())
                        .build())
                .build();
    }
}
