package com.microservice_level_up.error.not_enough_points;

public class DuplicatedProductCodeException extends RuntimeException {
    public DuplicatedProductCodeException(String message) {
        super(message);
    }

}
