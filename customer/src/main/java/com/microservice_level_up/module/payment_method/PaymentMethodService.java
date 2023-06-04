package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.customer.Customer;
import com.microservice_level_up.module.customer.ICustomerService;
import com.microservice_level_up.module.payment_method.dto.PaymentMethodRegistration;
import com.microservice_level_up.module.payment_method.dto.PaymentMethodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public record PaymentMethodService(
        PaymentMethodRepository repository,
        ICustomerService customerService) implements IPaymentMethodService {

    @Override
    public PaymentMethodResponse getById(long id) {
        return repository
                .findById(id)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found for this id: " + id));
    }

    @Override
    public Page<PaymentMethodResponse> getByCustomerId(long idCustomer, Pageable pageable) {
        return repository
                .findByCustomer(Customer.builder().id(idCustomer).build(), pageable)
                .map(this::mapResponse);
    }

    @Override
    public long add(PaymentMethodRegistration request) {
//        CustomerResponse customer = customerService.getById(request.customerId());
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .methodName(request.methodName())
                .cardNumber(request.cardNumber())
                .alias(request.alias())
                .expirationMonth(request.expirationMonth())
                .expirationYear(request.expirationYear())
                .cvv(request.cvv())
                .customer(Customer.builder().id(request.customerId()).build())
                .build();

        paymentMethod = repository.save(paymentMethod);
        return paymentMethod.getId();
    }

    @Override
    public void remove(long id) {
        repository.deleteById(id);
    }

    private PaymentMethodResponse mapResponse(PaymentMethod paymentMethod) {
        return new PaymentMethodResponse(
                paymentMethod.getId(),
                paymentMethod.getCustomer().getId(),
                paymentMethod.getMethodName(),
                paymentMethod.getCardNumber(),
                paymentMethod.getAlias(),
                paymentMethod.getExpirationMonth(),
                paymentMethod.getExpirationYear(),
                paymentMethod.getCvv()
        );
    }
}