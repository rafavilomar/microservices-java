package com.microservice_level_up.module.user;

import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import com.microservice_level_up.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public record UserController(IUserService userService) {

    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        userService.registerCustomer(request);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.CREATED,
                "Customer registered successfully"
        );
    }
}
