package com.microservice_level_up.error.duplicated_product_code;

public class DuplicatedProductCodeException extends RuntimeException {
    public DuplicatedProductCodeException(String message) {
        super(message);
    }

}
