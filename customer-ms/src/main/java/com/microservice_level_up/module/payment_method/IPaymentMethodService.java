package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.payment_method.dto.PaymentMethodRegistration;
import com.microservice_level_up.module.payment_method.dto.PaymentMethodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPaymentMethodService {
    PaymentMethodResponse getById(long id);
    Page<PaymentMethodResponse> getByCustomerId(long idCustomer, Pageable pageable);
    long add(PaymentMethodRegistration request);
    void remove(long id);
}
