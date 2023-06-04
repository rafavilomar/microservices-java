package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public record CustomerService(
        CustomerRepository repository,
        KafkaTemplate<String, Event<?>> producer) {

    private static String topicCustomer = "customers";

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .country(request.country())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customer = repository.save(customer);
//        publishCustomer(customer);
    }

    private void publishCustomer(Customer customer) {
        Event<Customer> event = new Event<>(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                EventType.CREATED,
                customer
        );
        producer.send(topicCustomer, event);
    }
}
