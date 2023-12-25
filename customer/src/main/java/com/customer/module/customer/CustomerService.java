package com.customer.module.customer;

import com.customer.kafka.events.Event;
import com.customer.kafka.events.EventType;
import com.customer.module.customer.dto.CustomerRegistrationRequest;
import com.customer.module.customer.dto.CustomerResponse;
import com.customer.module.customer.dto.CustomerUpdateRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public record CustomerService(
        CustomerRepository repository,
        KafkaTemplate<String, Event<?>> producer) implements ICustomerService {

    @Override
    public long register(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .country(request.country())
                .createdAt(LocalDateTime.now())
                .address(request.address())
                .build();

        customer = repository.save(customer);
        return customer.getId();
    }

    private void publishCustomer(Customer customer) {
        Event<Customer> event = new Event<>(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                EventType.CREATED,
                customer
        );
        String topicCustomer = "customers";
        producer.send(topicCustomer, event);
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
