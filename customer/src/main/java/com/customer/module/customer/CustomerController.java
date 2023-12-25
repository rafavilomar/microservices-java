package com.customer.module.customer;

import com.customer.module.customer.dto.CustomerRegistrationRequest;
import com.customer.module.customer.dto.CustomerResponse;
import com.customer.module.customer.dto.CustomerUpdateRequest;
import com.customer.response.BaseResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@SecurityRequirement(name = "bearerAuth")
public record CustomerController(ICustomerService service) {

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomerResponse>> getById(@PathVariable("id") long id) {
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
        long customerId = service.register(request);

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
        long customerId = service.update(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customerId,
                HttpStatus.OK,
                "Customer updated successfully"
        );
    }
}
