package com.microservice_level_up.module.user;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.InternalErrorException;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.module.role.IRoleService;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import com.microservice_level_up.module.user.dto.RegisterUserRequest;
import com.microservice_level_up.notification.CustomerCreatedNotification;
import common.grpc.common.CustomerRegistrationRequest;
import common.grpc.common.CustomerServiceGrpc;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public record UserService(
        UserRepository userRepository,
        IRoleService roleService,
        BCryptPasswordEncoder passwordEncoder,
        CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub,
        KafkaTemplate<String, Event<?>> producer) implements IUserService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCustomer(RegisterCustomerRequest newUser) {
        log.info("Register a new customer {}", newUser.email());

        User user = registerUser(RegisterUserRequest.builder()
                .roleName("Customer")
                .email(newUser.email())
                .password(newUser.password())
                .build());
        try {
            customerServiceBlockingStub.register(CustomerRegistrationRequest.newBuilder()
                    .setFirstName(newUser.firstName())
                    .setLastName(newUser.lastName())
                    .setEmail(newUser.email())
                    .setIdUser(user.getId())
                    .setAddress(newUser.address())
                    .setCountry(newUser.country())
                    .build());

            publishCustomer(newUser);

        } catch (Exception exception) {
            log.error("Error creating customer: {}", exception.getMessage());
            throw new InternalErrorException("Can't register in this moment. Please try later");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(RegisterUserRequest newUser) {
        log.info("Register new user {}", newUser);
        if (userRepository.existsByEmail(newUser.email()))
            throw new BadRequestException("Seems this email is already registered. Try login!");

        Role role = roleService.findByName(newUser.roleName());
        return userRepository.save(User.builder()
                .email(newUser.email())
                .password(passwordEncoder.encode(newUser.password()))
                .role(role)
                .build());
    }

    @Override
    public User getByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("There is not user for email: " + email));
    }

    private void publishCustomer(RegisterCustomerRequest newUser) {
        Event<CustomerCreatedNotification> event = new Event<>(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                EventType.CREATED,
                new CustomerCreatedNotification(newUser.firstName(), newUser.lastName(), newUser.email())
        );
        String topicCustomer = "customer_created";
        producer.send(topicCustomer, event);
    }
}
