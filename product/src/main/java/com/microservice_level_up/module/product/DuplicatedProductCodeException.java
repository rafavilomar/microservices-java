package com.microservice_level_up.module.product;

public class DuplicatedProductCodeException extends RuntimeException {
    public DuplicatedProductCodeException(String message) {
        super(message);
    }

}
