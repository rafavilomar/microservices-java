package com.microservice_level_up.error.duplicated_user_email;

public class DuplicatedUserEmailException extends RuntimeException {
    public DuplicatedUserEmailException(String message) {
        super(message);
    }

}
