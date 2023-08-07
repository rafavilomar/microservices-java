package com.microservice_level_up.error.not_enough_points;

public class NotEnoughPointsException extends RuntimeException {
    public NotEnoughPointsException(String message) {
        super(message);
    }

}
