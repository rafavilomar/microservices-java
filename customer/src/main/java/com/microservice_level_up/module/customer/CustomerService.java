package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.module.customer.dto.CustomerResponse;
import com.microservice_level_up.module.customer.dto.CustomerUpdateRequest;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public record CustomerService(CustomerRepository repository) implements ICustomerService {

    @Override
    public long register(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .country(request.country())
                .createdAt(LocalDateTime.now())
                .address(request.address())
                .idUser(request.idUser())
                .build();

        customer = repository.save(customer);
        return customer.getId();
    }

    @Override
    public CustomerResponse getById(long id) {
        return repository.findById(id)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found for this id: " + id));
    }

    private CustomerResponse mapResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCountry(),
                customer.getAddress()
        );
    }

    @Override
    public long update(CustomerUpdateRequest request) {
        Customer customer = repository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found for this id: " + request.id()));

        customer = Customer.builder()
                .id(customer.getId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .country(request.country())
                .address(request.address())
                .updatedAt(LocalDateTime.now())
                .createdAt(customer.getCreatedAt())
                .build();

        repository.save(customer);
        return customer.getId();
    }
}
