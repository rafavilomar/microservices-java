package com.microservice_level_up.error.http_exeption;

public class DuplicatedProductCodeException extends BadRequestException {
    public DuplicatedProductCodeException(String message) {
        super(message);
    }

}
