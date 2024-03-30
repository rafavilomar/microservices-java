# Handle Payment Method on Purchase Flow

Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-03-30

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