package com.microservice_level_up.module.invoice;

import com.microservice_level_up.dto.InvoicePaymentMethod;
import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.dto.PointsResponse;
import com.microservice_level_up.module.invoice.entity.Invoice;
import com.microservice_level_up.module.invoice.entity.Product;
import com.microservice_level_up.module.invoice.enums.InvoiceType;
import com.microservice_level_up.module.invoice.repository.InvoiceRepository;
import com.microservice_level_up.module.invoice.repository.ProductRepository;
import com.microservice_level_up.module.purchase.PurchaseRequest;
import common.grpc.common.CustomerResponse;
import common.grpc.common.CustomerServiceGrpc;
import common.grpc.common.PaymentMethodRequest;
import common.grpc.common.PaymentMethodResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public record InvoiceService (
        InvoiceRepository invoiceRepository,
        ProductRepository productRepository,
        CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub) implements IInvoiceService {

    @Override
    public InvoiceResponse save(
            CustomerResponse customer,
            String uuid,
            PurchaseRequest purchaseRequest,
            List<PointsResponse> pointsMovements) {
        log.info("Save invoice {} in database", uuid);

        PaymentMethodResponse paymentMethod = customerServiceBlockingStub
                .getPaymentMethodById(PaymentMethodRequest.newBuilder().setId(purchaseRequest.idPaymentMethod()).build());

        Invoice invoice = invoiceRepository.save(Invoice.builder()
                .uuid(uuid)
                .fullname(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .type(InvoiceType.PURCHASE)
                .subtotal(purchaseRequest.subtotal())
                .tax(purchaseRequest.tax())
                .total(purchaseRequest.total())
                .datetime(purchaseRequest.datetime())
                .idPaymentMethod(paymentMethod.getId())
                .build());

        List<Product> products = purchaseRequest.products().stream()
                .map(product -> Product.builder()
                        .code(product.code())
                        .price(product.price())
                        .quantity(product.quantity())
                        .invoice(invoice)
                        .build())
                .toList();
        productRepository.saveAll(products);

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .fullname(customer.getFirstName() + " " + customer.getLastName())
                .paymentMethod(new InvoicePaymentMethod(paymentMethod.getId(), paymentMethod.getMethodName()))
                .email(customer.getEmail())
                .products(purchaseRequest.products())
                .pointMovements(pointsMovements)
                .subtotal(purchaseRequest.subtotal())
                .total(purchaseRequest.total())
                .tax(purchaseRequest.tax())
                .datetime(purchaseRequest.datetime())
                .build();
    }

}
