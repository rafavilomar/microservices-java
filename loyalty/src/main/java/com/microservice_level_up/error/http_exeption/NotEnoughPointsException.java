package com.microservice_level_up.error.http_exeption;

public class NotEnoughPointsException extends BadRequestException {
    public NotEnoughPointsException(String message) {
        super(message);
    }

}
