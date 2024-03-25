package com.microservice_level_up.module.invoice;

import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.module.invoice.entity.Invoice;
import com.microservice_level_up.module.invoice.entity.Product;
import com.microservice_level_up.module.invoice.enums.InvoiceType;
import com.microservice_level_up.module.invoice.repository.InvoiceRepository;
import com.microservice_level_up.module.invoice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public record InvoiceService (
        InvoiceRepository invoiceRepository,
        ProductRepository productRepository) implements IInvoiceService {

    @Override
    public void save(InvoiceResponse invoiceResponse, String uuid) {
        log.info("Save invoice {} in database", uuid);

        Invoice invoice = invoiceRepository.save(Invoice.builder()
                .uuid(uuid)
                .fullname(invoiceResponse.fullname())
                .email(invoiceResponse.email())
                .type(InvoiceType.PURCHASE)
                .subtotal(invoiceResponse.subtotal())
                .tax(invoiceResponse.tax())
                .total(invoiceResponse.total())
                .datetime(invoiceResponse.datetime())
                .build());

        List<Product> products = invoiceResponse.products().stream()
                .map(product -> Product.builder()
                        .code(product.code())
                        .price(product.price())
                        .quantity(product.quantity())
                        .invoice(invoice)
                        .build())
                .toList();

        productRepository.saveAll(products);
    }

}
