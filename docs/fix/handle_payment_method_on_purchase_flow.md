# Handle Payment Method on Purchase Flow

Author: rafavilomar  
Status: `Finished` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-04-06

## Contents

- Objective
- Solution
  - Validate payment method on `PurchaseService`
  - Add invoice ID for notification
- Considerations

## Objective

We need to handle the payment method during the purchase process, and it also seems like the invoice ID is not available 
for the email notification.

## Solution

### Validate payment method on `PurchaseService`

Now there is a new Grpc service to get payment methods by ID. This service is being used by the `InvoiceService` before 
save the invoice.

```java
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
```

### Add invoice ID for notification

The `InvoiceResponse` was built before save the invoice and generate de ID, but now the `PurchaseService` got this 
response once the invoice information is saved.

```java
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

        InvoiceResponse invoiceResponse = invoiceService.save(
                customer,
                uuid,
                request,
                getPointsResponse(redemptionPointsResponse, accumulationPointsResponse));

        sendEmailNotification(invoiceResponse);

        return invoiceResponse;
}
```

## Considerations

This fix comes from feature [Purchase Flow](../features/purchase_flow.md)