package com.microservice_level_up.module.invoice.repository;

import com.microservice_level_up.module.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
