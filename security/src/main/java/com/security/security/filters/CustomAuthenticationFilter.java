package com.security.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.module.auth.IAuthService;
import com.security.module.auth.dtos.LoginRequestDto;
import com.security.module.auth.dtos.LoginResponseDTO;
import com.security.response.BaseResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final IAuthService authService;

    /**
     * Create a new UsernamePasswordAuthenticationToken for the username parameter from the given request.
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(),
                loginRequest.password()
        );

        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * Generate a new access and refresh token for the logged user. Those token are based on user's name and permissions
     * codes.
     *
     * @see IAuthService#generateRefreshToken(User)
     * @see IAuthService#generateAccessToken(User)
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) throws IOException {

        User user = (User) authentication.getPrincipal();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<BaseResponse<LoginResponseDTO>> result = new BaseResponse<LoginResponseDTO>().buildResponseEntity(
                authService.login(user),
                HttpStatus.OK,
                "Login successful"
        );

        new ObjectMapper().writeValue(response.getOutputStream(), result.getBody());
    }
}
