package com.microservice_level_up.error.http_exeption;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){super(message);}
}
