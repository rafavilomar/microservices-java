package com.microservice_level_up.module.customer;

import com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest;
import com.microservice_level_up.module.customer.dto.CustomerResponseDTO;
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
    public ResponseEntity<BaseResponse<CustomerResponseDTO>> getById(@PathVariable("id") Integer id) {
        log.info("Get customer by id {}", id);
        CustomerResponseDTO customer = service.getById(id);

        BaseResponse<CustomerResponseDTO> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customer,
                HttpStatus.OK,
                "Customer found"
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Integer>> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("New customer registration {}", request);
        int customerId = service.registerCustomer(request);

        BaseResponse<Integer> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customerId,
                HttpStatus.OK,
                "Customer registered successfully"
        );
    }

    @PutMapping
    public void updateCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("New customer registration {}", request);
        service.registerCustomer(request);
    }
}
