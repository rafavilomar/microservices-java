# Implement API Gateway
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-04-07

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

## Considerations
