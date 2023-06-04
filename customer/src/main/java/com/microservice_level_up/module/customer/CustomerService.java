package com.microservice_level_up.module.customer;

import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.module.customer.dto.CustomerResponseDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public record CustomerService(
        CustomerRepository repository,
        KafkaTemplate<String, Event<?>> producer) {

    public int registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .country(request.country())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customer = repository.save(customer);
        return customer.getId();
//        publishCustomer(customer);
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

    public CustomerResponseDTO getById(Integer id) {
        return repository.findById(id)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found for this id: " + id));
    }

    private CustomerResponseDTO mapResponse(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCountry()
        );
    }
}
