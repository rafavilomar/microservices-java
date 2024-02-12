package com.microservice_level_up.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice_level_up.error.ExceptionResponse;
import com.microservice_level_up.module.auth.IAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final IAuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        ResponseEntity<ExceptionResponse> responseBody;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        /*
         * If any request is trying to access any public it should avoid all the filter validation and go ahead with
         * the original request
         */
        if (isPublicPath(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * If the original request doesn't have the authorization header, or it isn't a Bearer token,
         * should return an unauthorized response
         */
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseBody = unauthorizedResponseInvalidToken();

            new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
            return;
        }

        try {

            /*
             * If the token is present and is valid load the authentication to the security context
             * and go ahead with the original request
             */
            String token = authorizationHeader.substring("Bearer ".length());
            String email = authService.getEmail(token);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    authService.getPermissions(token));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("Logged user {} trying to access: {}", email, request.getServletPath());
            filterChain.doFilter(request, response);
            return;

        } catch (TokenExpiredException e) {
            log.error("Error logging in: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseBody = tokenExpiredResponse();

        } catch (Exception e) {
            log.error("Error logging in: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseBody = unauthorizedResponseInvalidToken();
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }

    private boolean isPublicPath(String path) {
        return path.contains("/login") ||
                path.contains("/api/v1/auth/refrescarToken");
    }

    private ResponseEntity<ExceptionResponse> unauthorizedResponseInvalidToken() {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                List.of("Invalid provided access token")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> tokenExpiredResponse() {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                List.of("The provided access token is already expired")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
