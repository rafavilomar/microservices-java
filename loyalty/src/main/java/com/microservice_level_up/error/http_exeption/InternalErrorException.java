package com.microservice_level_up.error.http_exeption;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String message){super(message);}
}
