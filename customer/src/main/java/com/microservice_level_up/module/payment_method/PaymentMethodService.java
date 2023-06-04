package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.payment_method.dto.PaymentMethodRegistration;
import com.microservice_level_up.module.payment_method.dto.PaymentMethodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public record PaymentMethodService() implements IPaymentMethodService {

    @Override
    public PaymentMethodResponse getById(long id) {
        return null;
    }

    @Override
    public Page<PaymentMethodResponse> getByCustomerId(long idCustomer, Pageable pageable) {
        return null;
    }

    @Override
    public long add(PaymentMethodRegistration request) {
        return 0;
    }

    @Override
    public void remove(long id) {

    }
}
