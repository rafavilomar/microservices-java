package com.microservice_level_up.module.invoice.repository;

import com.microservice_level_up.module.invoice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
