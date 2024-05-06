package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.payment_method.dto.PaymentMethodRegistration;
import com.microservice_level_up.module.payment_method.dto.PaymentMethodResponse;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment Method")
@Slf4j
@RestController
@RequestMapping("/api/v1/payment-method")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final IPaymentMethodService service;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PAYMENT_METHOD')")
    @Operation(summary = "Add a new payment-method")
    public ResponseEntity<BaseResponse<Long>> add(@Valid @RequestBody PaymentMethodRegistration request) {
        log.info("Add new payment method {}", request);
        long payload = service.add(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Payment method registered"
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_PAYMENT_METHOD')")
    @Operation(summary = "Get payment-method by ID")
    public ResponseEntity<BaseResponse<PaymentMethodResponse>> getById(@PathVariable("id") long id) {
        log.info("Get payment method by id {}", id);
        PaymentMethodResponse payload = service.getById(id);

        BaseResponse<PaymentMethodResponse> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Payment method found"
        );
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('GET_CUSTOMER_PAYMENT_METHOD')")
    @Operation(summary = "Get paged payment-methods by customer's ID")
    public ResponseEntity<BaseResponse<Page<PaymentMethodResponse>>> getByCustomerId(
            @PathVariable("customerId") long customerId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        log.info("Get payment method by customer's id {}", customerId);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PaymentMethodResponse> payload = service.getByCustomerId(customerId, pageable);

        BaseResponse<Page<PaymentMethodResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Payment methods found for this customer"
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REMOVE_PAYMENT_METHOD')")
    @Operation(summary = "Remove payment-method")
    public ResponseEntity<BaseResponse<Void>> remove(@PathVariable("id") long id) {
        log.info("Remove payment method by id {}", id);
        service.remove(id);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.NO_CONTENT,
                "Payment method removed"
        );
    }

}
