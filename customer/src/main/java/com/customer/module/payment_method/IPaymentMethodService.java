package com.customer.module.payment_method;

import com.customer.module.payment_method.dto.PaymentMethodRegistration;
import com.customer.module.payment_method.dto.PaymentMethodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPaymentMethodService {
    PaymentMethodResponse getById(long id);

    Page<PaymentMethodResponse> getByCustomerId(long idCustomer, Pageable pageable);

    long add(PaymentMethodRegistration request);

    void remove(long id);
}
