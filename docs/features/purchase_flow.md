# Purchase Flow

Author: rafavilomar  
Status: `In review` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-03-27

## Contents

- Objective
- Goals
- No goals
- Overview
- Solution
  - Create Shopping microservice
  - Purchase service
  - Invoice UUIDs
  - Purchase email notification
- Considerations

## Objective

For each purchase is necessary to handle product inventory, loyalty points and email notifications. So, it's necessary 
to use Grpc and Kafka to microservices interactions from a new microservice called Shopping.

## Goals

- Add a new microservice Shopping
- Use grpc and kafka for microservices communication.
- Validate inventory and lot points.
- Send email notification

## No goals

- Add a shopping cart.
- Customize notification email.

## Overview

![Purchase flow](../images/purchase_flow.png)

## Solution

### Create Shopping microservice

This new microservice will be dedicated for all shopping services such as purchase, refund and cart; but for now only 
the purchase feature will be implemented.

### Purchase service

Purchase service needs to call customer, product and points services. And also send the proper email notification to 
customer. This is the first version of this service:

```java
public InvoiceResponse purchase(PurchaseRequest request) {
    String uuid = UUID.randomUUID().toString();
    log.info("================== New purchase {} ==================", uuid);
    log.info("Purchase request for uuid {}: {}", uuid, request);

    CustomerResponse customer = customerServiceBlockingStub.getById(CustomerRequest.newBuilder()
            .setId(request.idCustomer())
            .build());
    PointsResponse redemptionPointsResponse = redeemPoints(request);
    buyProducts(request.products());
    PointsResponse accumulationPointsResponse = accumulatePoints(request);

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
```

Once the purchase is processed we can persist it in the database based on the invoice response, just like this for 
`InvoiceService`:

```java
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
```

### Invoice UUIDs

We can see how the UUID class is used for the purchase service. This UUID is used to track purchase logs and register 
points movements for **Loyalty**. This is because t classic sequence ID (that we're still using as primary key) is 
generated during persistence process; but we need a unique value before save the invoice.

### Purchase email notification

This `PurchaseEmail` service from the Email Notification microservice get the invoice with all necessary information 
about the purchase and customer information to send it through email. For now, this notification consist of a simple 
text message, but it will be replaced soon.

```java
@KafkaListener(
        topics = "purchase",
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "grupo1"
)
public void sendEmail(Event<?> event) {
    PurchaseNotification purchase = objectMapper.convertValue(event.data(), PurchaseNotification.class);
    log.info("Send purchase email {}", purchase);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(purchase.invoice().email());
    message.setSubject("Invoice");
    message.setText("Purchase's invoice");

    mailSender.send(message);
}
```

As we can see, all purchase notification will be listening on the kafka topic `purchase`, so customers don't need to wait 
for this email to complete the purchase.

## Considerations

During this development was necessary to add some simple security configuration for some microservices. It seems that 
every dependency from the Common module are being inherit by all the other modules.

That's why the `spring-boot-starter-data-jpa` was replaced by `jakarta.persistence-api` to keep using jakarta annotations 
for DTOs on that module without forcing other modules to use a database connection. In the future, the dependency 
`spring-boot-starter-security` needs to be removed from Common module.