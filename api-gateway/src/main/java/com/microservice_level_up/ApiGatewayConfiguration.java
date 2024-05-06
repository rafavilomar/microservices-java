package com.microservice_level_up;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class ApiGatewayConfiguration {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("security", r -> r.path("/security/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://security"))
                .route("customer", r -> r.path("/customer/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://customer"))
                .route("loyalty", r -> r.path("/loyalty/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://loyalty"))
                .route("product", r -> r.path("/product/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://product"))
                .route("shopping", r -> r.path("/shopping/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://shopping"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(source);
    }
}
