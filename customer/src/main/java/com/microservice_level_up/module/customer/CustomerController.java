package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.module.customer.dto.CustomerResponse;
import com.microservice_level_up.module.customer.dto.CustomerUpdateRequest;
import com.microservice_level_up.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
public record CustomerController(CustomerService service) {

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomerResponse>> getById(@PathVariable("id") Long id) {
        log.info("Get customer by id {}", id);
        CustomerResponse customer = service.getById(id);

        BaseResponse<CustomerResponse> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customer,
                HttpStatus.OK,
                "Customer found"
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("New customer registration {}", request);
        long customerId = service.registerCustomer(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customerId,
                HttpStatus.CREATED,
                "Customer registered successfully"
        );
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Long>> updateCustomer(@Valid @RequestBody CustomerUpdateRequest request) {
        log.info("Update customer {}", request);
        long customerId = service.updateCustomer(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customerId,
                HttpStatus.OK,
                "Customer updated successfully"
        );
    }
}
