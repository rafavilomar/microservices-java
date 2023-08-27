package com.microservice_level_up.error.conflict;

public class DuplicatedCategoryException extends RuntimeException {
    public DuplicatedCategoryException(String message) {
        super(message);
    }

}
