package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Page<PaymentMethod> findByCustomer(Customer customer, Pageable pageable);
}
