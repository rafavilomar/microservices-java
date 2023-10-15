package com.microservice_level_up.error.conflict;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

}
