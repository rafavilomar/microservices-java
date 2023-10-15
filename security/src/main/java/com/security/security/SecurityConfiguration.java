package com.security.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.error.ExceptionResponse;
import com.security.module.auth.IAuthService;
import com.security.security.filters.CustomAuthenticationFilter;
import com.security.security.filters.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final UserDetailsService userDetailsService;
    private final AuthenticationConfiguration authConfiguration;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IAuthService authService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return authProvider;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(List.of(CorsConfiguration.ALL));
                        configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
                        configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
                        return configuration;
                    };
                    cors.configurationSource(source);
                })
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000) // a year
                                .preload(true)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            ResponseEntity<ExceptionResponse> result = new ResponseEntity<>(new ExceptionResponse(
                                    LocalDateTime.now(),
                                    HttpStatus.UNAUTHORIZED.value(),
                                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                    List.of("Authentication failed")
                            ), HttpStatus.UNAUTHORIZED);

                            response.setStatus(401);
                            new ObjectMapper().writeValue(response.getOutputStream(), result);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/refreshToken").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/logged").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v2/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilter(new CustomAuthenticationFilter(
                        authenticationManager(authConfiguration),
                        authService))
                .addFilterBefore(
                        new CustomAuthorizationFilter(authService),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}
