package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.payment_method.dto.PaymentMethodRegistration;
import com.microservice_level_up.module.payment_method.dto.PaymentMethodResponse;
import com.microservice_level_up.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment-method")
public record PaymentMethodController(IPaymentMethodService service) {

    @PostMapping
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
    public ResponseEntity<BaseResponse<Page<PaymentMethodResponse>>> getByCustomerId(
            @PathVariable("customerId") long customerId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        log.info("Get payment method by customer's id {}", customerId);
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentMethodResponse> payload = service.getByCustomerId(customerId, pageable);

        BaseResponse<Page<PaymentMethodResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Payment methods found for this customer"
        );
    }

    @DeleteMapping("/{id}")
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
