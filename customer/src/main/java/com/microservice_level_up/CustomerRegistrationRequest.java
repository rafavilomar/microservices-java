package com.microservice_level_up;

public record CustomerRegistrationRequest (
    String firstName,
    String lastName,
    String email){
}
