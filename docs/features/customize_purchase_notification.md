# Customize purchase notification

Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-03-28

## Contents

- Objective
- Goals
- No goals
- Overview
- Solution
  - HTML template
  - Using template
- Considerations

## Objective

The invoice information must be available on the email notification. So. it's necessary to handle a new template as an 
HTML file and then replace all the information before send the message.

## Goals

- Create a purchase notification template

## Overview

![Invoice preview](..%2Fimages%2Finvoice_purchase.png)

This is based on the template example by `@WesCossick` posted [HERE](https://github.com/sparksuite/simple-html-invoice-template)

## Solution

### HTML template

This template has been added in the following directory for Email Notification microservice: 
`src/main/resources/templates/purchase_invoice.html`. This file is a basic html with some CSS styles, and also has some 
replaceable variables with the following format: `${variable}`.

```html
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Invoice</title>

        <style>
            // styles here...
        </style>
    </head>

    <body>
    <div class="invoice-box">
      <table cellpadding="0" cellspacing="0">
        <tr class="top">
          <td colspan="2">
            <table>
              <tr>
                <td class="title">
                  <img
                          src="https://staging.cspcomputer.store/wp-content/plugins/elementorpro3171n/assets/images/logo-placeholder.png"
                          style="width: 100%; max-width: 300px"
                  />
                </td>
                <td class="text-left">
                  Invoice #: ${id}<br />
                  Date: ${date}
                </td>
              </tr>
            </table>
          </td>
        </tr>

        <tr class="information">
          <td colspan="2">
            <table>
              <tr>
                <td>Company Inc.<br/> Company address</td>
                <td class="text-left">${fullname}<br />${email}</td>
              </tr>
            </table>
          </td>
        </tr>

        <tr class="heading">
          <td>Payment Method</td>
          <td></td>
        </tr>

        <tr class="details">
          <td>${paymentMethodName}</td>
          <td></td>
        </tr>

        <tr class="heading">
          <td>Points movements</td>
          <td class="text-left">Amount</td>
        </tr>
        ${pointMovements}

        <tr class="heading">
          <td>Product</td>
          <td class="text-left">Price</td>
        </tr>
        ${products}

        <tr class="subtotal">
          <td></td>
          <td class="text-left">Subtotal: $${subtotal}</td>
        </tr>
        <tr>
          <td></td>
          <td class="text-left">Tax: $${tax}</td>
        </tr>
        <tr class="total">
          <td></td>
          <td class="text-left">Total: $${total}</td>
        </tr>
      </table>
    </div>
    </body>
</html>
```

### Using template

Now our `PurchaseEmailService` uses the template `purchase_invoice.html` and replace all the necessaries variables:

```java
public void sendEmail(Event<?> event) throws IOException, MessagingException {
        PurchaseNotification purchase = objectMapper.convertValue(event.data(), PurchaseNotification.class);
        log.info("Send purchase email {}", purchase);
      
        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipients(Message.RecipientType.TO, purchase.invoice().email());
        message.setSubject("Invoice");
      
        String template = Files.readString(Paths.get(
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("templates/purchase_invoice.html")
                        .getPath()
        ));
        template = template
                .replace("${id}", String.valueOf(purchase.invoice().id()))
                .replace("${date}", purchase.invoice().datetime().toLocalDate().toString())
                .replace("${fullname}", purchase.invoice().fullname())
                .replace("${email}", purchase.invoice().email())
                .replace("${paymentMethodName}", purchase.invoice().paymentMethod().name())
                .replace("${pointMovements}", getTableRowsForPointsMovements(purchase.invoice().pointMovements()))
                .replace("${products}", getTableRowsForProducts(purchase.invoice().products()))
                .replace("${subtotal}", String.valueOf(purchase.invoice().subtotal()))
                .replace("${tax}", String.valueOf(purchase.invoice().tax()))
                .replace("${total}", String.valueOf(purchase.invoice().total()));
      
        message.setContent(template, "text/html; charset=utf-8");
      
        mailSender.send(message);
}
```
