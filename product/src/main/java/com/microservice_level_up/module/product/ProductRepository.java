package com.microservice_level_up.module.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);

    List<Product> findByCodeIn(List<String> code);

    Page<Product> findAllByNameOrCodeOrCategory_Name(
            String productName,
            String productCode,
            String categoryName,
            Pageable pageable);
}
