package com.microservice_level_up.module.user;

import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import com.microservice_level_up.module.user.dto.RegisterUserRequest;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping()
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Register a new user")
    public ResponseEntity<BaseResponse<Void>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        userService.registerUser(request);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.CREATED,
                "User registered successfully"
        );
    }

    @PostMapping("/customer")
    @Operation(summary = "Register a new customer")
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
