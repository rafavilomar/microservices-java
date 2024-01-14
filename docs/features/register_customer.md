# Tile
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-01-13

## Contents
- Goals
- Background
- Overview
- Detailed Design
  - Solution 1
    - Roles and permissions structure
    - Spring Security implementation
    - Permissions validation
    - Save user
    - Save customer
    - Send email
- Considerations

## Links
- [Example implementation with gRPC and SpringBoot #1](https://medium.com/@ankithahjpgowda/grpc-implementation-in-springboot-and-microservices-366dc7a66c5a)
- [Example implementation with gRPC and SpringBoot #2](https://www.linkedin.com/pulse/building-microservices-spring-boot-andgrpc-jonathan-manera/)

## Objective
Lately, I've been using Eureka to handle microservices communication and centralizing security stuff in just one microservice.

The principal goal is to distribuite permissions validations across all microservices and use gRPC to handle a faster instant communication between microservices, and also use Kafka for asynchronously event driven communication with email services.

## Goals
- Implement Spring Security filters.
- Implement gRPC to communicate Security with Customer.
- Implement Kafka to communicate Security with Email Notification

## Background

## Overview
![Customer register flow](..%2Fimages%2Fcustomer_register_flow.png)

## Solution 1

### Roles and permissions structure
Roles consist of a list of permissions that define user's access to the system configured in controllers. In this way roles are totally flexible to permissions changes.
Each user can only have one role assigned.
![Role Structure](..%2Fimages%2Frole_structure.png)

### Spring Security implementation
### Permissions validation
### Save user
### Save customer
### Send email