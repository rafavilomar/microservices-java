package com.microservice_level_up.module.purchase;

import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.enums.MovementType;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.module.invoice.IInvoiceService;
import com.microservice_level_up.notification.PurchaseNotification;
import common.grpc.common.PointsResponse;
import common.grpc.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public record PurchaseService (
        CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub,
        ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub,
        LoyaltyServiceGrpc.LoyaltyServiceBlockingStub loyaltyServiceBlockingStub,
        KafkaTemplate<String, Event<?>> producer,
        IInvoiceService invoiceService) implements IPurchaseService{

    @Override
    public InvoiceResponse purchase(PurchaseRequest request) {
        String uuid = UUID.randomUUID().toString();
        log.info("================== New purchase {} ==================", uuid);
        log.info("Purchase request for uuid {}: {}", uuid, request);

        CustomerResponse customer = customerServiceBlockingStub.getCustomerById(CustomerRequest.newBuilder()
                .setId(request.idCustomer())
                .build());
        PointsResponse redemptionPointsResponse = redeemPoints(request, uuid);
        buyProducts(request.products());
        PointsResponse accumulationPointsResponse = accumulatePoints(request, uuid);

        log.info("================== Purchase finished {} ==================", uuid);

        InvoiceResponse invoiceResponse = InvoiceResponse.builder()
                .fullname(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .products(request.products())
                .pointMovements(getPointsResponse(redemptionPointsResponse, accumulationPointsResponse))
                .subtotal(request.subtotal())
                .total(request.total())
                .tax(request.tax())
                .datetime(request.datetime())
                .build();

        invoiceService.save(invoiceResponse, uuid);

        sendEmailNotification(invoiceResponse);

        return invoiceResponse;
    }

    private PointsResponse redeemPoints(PurchaseRequest request, String uuid){
        return loyaltyServiceBlockingStub
                .redeemPoints(common.grpc.common.PurchaseRequest.newBuilder()
                        .setIdCustomer(request.idCustomer())
                        .setPoints(request.pointsRedemption())
                        .setMovementDate(request.datetime().toString())
                        .setInvoiceUuid(uuid)
                        .build());
    }

    private PointsResponse accumulatePoints(PurchaseRequest request, String uuid){
        return loyaltyServiceBlockingStub
                .accumulatePoints(common.grpc.common.PurchaseRequest.newBuilder()
                        .setIdCustomer(request.idCustomer())
                        .setDollar(request.subtotal())
                        .setMovementDate(request.datetime().toString())
                        .setInvoiceUuid(uuid)
                        .build());
    }

    private void buyProducts(List<com.microservice_level_up.dto.BuyProductRequest> requestProducts) {
        List<Product> products = requestProducts
                .stream()
                .map(product -> Product.newBuilder().setCode(product.code()).setQuantity(product.quantity()).build())
                .toList();
        productServiceBlockingStub.buyProduct(BuyProductRequest.newBuilder().addAllProducts(products).build());
    }

    private List<com.microservice_level_up.dto.PointsResponse> getPointsResponse(
            PointsResponse redemptionPointsResponse,
            PointsResponse accumulationPointsResponse){

        return List.of(
                new com.microservice_level_up.dto.PointsResponse(
                        redemptionPointsResponse.getPoints(),
                        redemptionPointsResponse.getDollar(),
                        MovementType.valueOf(redemptionPointsResponse.getType())),
                new com.microservice_level_up.dto.PointsResponse(
                        accumulationPointsResponse.getPoints(),
                        accumulationPointsResponse.getDollar(),
                        MovementType.valueOf(accumulationPointsResponse.getType()))
        );
    }

    private void sendEmailNotification(InvoiceResponse invoice) {
        Event<PurchaseNotification> event = new Event<>(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                EventType.CREATED,
                new PurchaseNotification(invoice)
        );
        producer.send("purchase", event);
    }
}
