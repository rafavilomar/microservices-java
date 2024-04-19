package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerResponse;
import com.microservice_level_up.module.customer.dto.CustomerUpdateRequest;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer")
@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
public record CustomerController(ICustomerService service) {

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
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

    @PutMapping
    @Operation(summary = "Update customer")
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
