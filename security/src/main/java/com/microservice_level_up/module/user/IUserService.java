package com.microservice_level_up.module.user;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.InternalErrorException;
import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;

public interface IUserService {

    /**
     * Create a new user as a customer.
     * @param newUser New user's information.
     * @throws BadRequestException If there is another user for the given email.
     * @throws InternalErrorException If there is any error consulting customer role, creating customer or sending email.
     */
    void registerCustomer(RegisterCustomerRequest newUser);
}
