package com.microservice_level_up.module.auth;

import com.microservice_level_up.module.auth.dtos.TokensResponseDTO;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/v1/auth")
public record AuthController (IAuthService authService) {

    @GetMapping("/refreshToken")
    @Operation(summary = "Refresh bearer token")
    public ResponseEntity<BaseResponse<TokensResponseDTO>> refreshToken(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        BaseResponse<TokensResponseDTO> response = new BaseResponse<>();
        return response.buildResponseEntity(
                authService.refreshToken(authorizationHeader),
                HttpStatus.OK,
                "Token refreshed"
        );
    }
}
