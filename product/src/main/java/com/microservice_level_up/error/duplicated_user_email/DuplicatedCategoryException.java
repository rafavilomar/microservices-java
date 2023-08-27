package com.microservice_level_up.error.duplicated_user_email;

public class DuplicatedCategoryException extends RuntimeException {
    public DuplicatedCategoryException(String message) {
        super(message);
    }

}
