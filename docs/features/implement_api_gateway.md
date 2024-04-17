# Implement API Gateway
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-04-16

## Contents
- Objective
- Overview
- Solution
  - Add new module
  - Configure routing
- Considerations

## Objective

It's necessary to add an API Gateway as a unique entry point to all system. 

## Overview

## Solution

### Add new module

The API Gateway will be running on port `8765` and connected to Eureka server as another client to know the location of 
all microservices.

```properties
server:
  port: 8765

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
    fetch-registry: true
    register-with-eureka: true
    enabled: true

```

### Configure routing

All microservices, beside `Email Notification`, have been added to the routing configuration.

```java
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
}
```

## Considerations

The cors configuration have been removed for `Security`, `Product` and `Shopping` microservices. Only `API Gateway` will 
handle it.