package com.microservice_level_up.error.duplicated_category;

public class DuplicatedCategoryException extends RuntimeException {
    public DuplicatedCategoryException(String message) {
        super(message);
    }

}
