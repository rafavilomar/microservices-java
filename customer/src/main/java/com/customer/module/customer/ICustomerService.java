package com.customer.module.customer;

import com.customer.module.customer.dto.CustomerRegistrationRequest;
import com.customer.module.customer.dto.CustomerResponse;
import com.customer.module.customer.dto.CustomerUpdateRequest;

public interface ICustomerService {
    long register(CustomerRegistrationRequest request);

    CustomerResponse getById(long id);

    long update(CustomerUpdateRequest request);
}
