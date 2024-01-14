package com.microservice_level_up.module.user;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.InternalErrorException;
import com.microservice_level_up.module.role.IRoleService;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.UnexpectedException;

@Slf4j
@Service
public record UserService(
        UserRepository userRepository,
        IRoleService roleService,
        BCryptPasswordEncoder passwordEncoder) implements IUserService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCustomer(RegisterCustomerRequest newUser) {
        log.info("Register a new customer {}", newUser.email());
        if (userRepository.existsByEmail(newUser.email()))
            throw new BadRequestException("Seems this email is already registered. Try login!");

        try {
            Role customerRole = roleService.findByName("Customer");
            User user = User.builder()
                    .email(newUser.email())
                    .password(passwordEncoder.encode(newUser.password()))
                    .role(customerRole)
                    .build();
            userRepository.save(user);
        } catch (Exception exception) {
            log.error("Error creating customer: {}", exception.getMessage());
            throw new InternalErrorException("Can't register in this moment. Please try later");
        }
    }
}