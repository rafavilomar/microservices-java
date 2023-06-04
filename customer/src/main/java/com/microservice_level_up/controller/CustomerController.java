package com.microservice_level_up.controller;

import com.microservice_level_up.dto.CustomerRegistrationRequest;
import com.microservice_level_up.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
public record CustomerController(CustomerService customerService) {

    @PostMapping
    public void registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("New customer registration {}", request);
        customerService.registerCustomer(request);
    }
}
