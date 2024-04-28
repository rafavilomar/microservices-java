package com.microservice_level_up.error.http_exeption;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message){super(message);}
}
