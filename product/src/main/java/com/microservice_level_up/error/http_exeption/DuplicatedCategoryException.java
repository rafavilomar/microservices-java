package com.microservice_level_up.error.http_exeption;

public class DuplicatedCategoryException extends BadRequestException {
    public DuplicatedCategoryException(String message) {
        super(message);
    }

}
