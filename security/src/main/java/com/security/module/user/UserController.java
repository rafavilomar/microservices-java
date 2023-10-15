package com.security.module.user;

import com.security.module.user.dto.NewUserDto;
import com.security.module.user.dto.UserResponseDto;
import com.security.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User")
@RestController
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = "bearerAuth")
public record UserController(IUserService userService) {

    @PostMapping
    @Operation(summary = "Create new user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BaseResponse<Long>> createUser(@RequestBody @Valid NewUserDto newUser) {

        Long payload = userService.create(newUser);
        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Created user"
        );
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BaseResponse<UserResponseDto>> findByEmail(@RequestParam("email") String email) {

        UserResponseDto payload = userService.findByEmail(email);
        BaseResponse<UserResponseDto> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Found user"
        );
    }

    @GetMapping("/logged")
    @Operation(summary = "Get logged user")
    public ResponseEntity<BaseResponse<UserResponseDto>> getLoggedUser() {

        UserResponseDto payload = userService.getLoggedUserResponse();
        BaseResponse<UserResponseDto> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Found logged user"
        );
    }
}
