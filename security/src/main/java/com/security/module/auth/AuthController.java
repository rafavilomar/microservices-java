package com.security.module.auth;

import com.security.module.auth.dtos.TokensResponseDTO;
import com.security.response.BaseResponse;
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

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
public record AuthController(IAuthService authService) {

    @GetMapping("/refreshToken")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<BaseResponse<TokensResponseDTO>> refreshToken(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        TokensResponseDTO payload = authService.refreshToken(authorizationHeader);

        BaseResponse<TokensResponseDTO> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Customer found"
        );
    }
}
