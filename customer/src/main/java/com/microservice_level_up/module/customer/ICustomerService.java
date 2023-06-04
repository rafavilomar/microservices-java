package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.module.customer.dto.CustomerResponse;
import com.microservice_level_up.module.customer.dto.CustomerUpdateRequest;

public interface ICustomerService {
    long register(CustomerRegistrationRequest request);

    CustomerResponse getById(Long id);

    long update(CustomerUpdateRequest request);
}
