package com.microservice_level_up.module.user;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.InternalErrorException;
import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import com.microservice_level_up.module.user.dto.RegisterUserRequest;

public interface IUserService {

    /**
     * Create a new user as a customer.
     * @param newUser New user's information.
     * @throws InternalErrorException If there is any error creating customer or sending email.
     * @see #registerUser(RegisterUserRequest)
     */
    void registerCustomer(RegisterCustomerRequest newUser);

    /**
     * Create a new internal user.
     * @param newUser New user's information.
     * @throws BadRequestException If there is another user for the given email.
     * @throws jakarta.persistence.EntityNotFoundException If the given role name doesn't exist.
     */
    User registerUser(RegisterUserRequest newUser);

    User getByEmail(String email);
}
