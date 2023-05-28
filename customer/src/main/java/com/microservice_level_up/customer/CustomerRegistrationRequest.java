package com.microservice_level_up.customer;

public record CustomerRegistrationRequest (
    String firstName,
    String lastName,
    String email){
}
