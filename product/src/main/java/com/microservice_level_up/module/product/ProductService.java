package com.microservice_level_up.module.product;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.DuplicatedProductCodeException;
import com.microservice_level_up.module.category.Category;
import com.microservice_level_up.module.category.ICategoryService;
import com.microservice_level_up.dto.CategoryResponse;
import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.module.product.dto.FilterProductRequest;
import com.microservice_level_up.module.product.dto.ProductRegistrationRequest;
import com.microservice_level_up.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void buy(List<BuyProductRequest> buyProducts) {
        List<String> codes = buyProducts.stream().map(BuyProductRequest::code).toList();
        List<Product> products = repository.findByCodeIn(codes);
        if (products.size() != buyProducts.size()) validateCodeExistence(codes, products);

        int index = 0;
        for (Product product : products) {
            int quantityToSubtract = buyProducts.get(index).quantity();
            if (quantityToSubtract > product.getStock())
                throw new BadRequestException("Not enough "+product.getCode()+" products in stock to subtract "+quantityToSubtract);
            product.setStock(product.getStock() - quantityToSubtract);
        }

        repository.saveAll(products);
    }

    @Override
    public Page<ProductResponse> filter(FilterProductRequest request) {
        Pageable pageable = PageRequest.of(request.page() - 1, request.size());
        return repository.findAllByNameOrCodeOrCategory_Name(
                        request.productName(),
                        request.productCode(),
                        request.categoryName(),
                        pageable)
                .map(this::mapResponse);
    }

    @Override
    public ProductResponse getByCode(String code) {
        return repository.findByCode(code)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found for this code: " + code));

    }

    private void validateCodeExistence(List<String> codes, List<Product> products) {
        codes = new ArrayList<>(codes);
        codes.removeAll(products.stream().map(Product::getCode).toList());
        if (!codes.isEmpty()) {
            throw new EntityNotFoundException("Products " + codes + " do not exist");
        }
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
