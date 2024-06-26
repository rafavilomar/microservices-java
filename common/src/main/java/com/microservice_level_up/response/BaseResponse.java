package com.microservice_level_up.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class BaseResponse<T> {

    private int status;
    private String statusDescription;
    private String message;
    private T payload;

    public ResponseEntity<BaseResponse<T>> buildResponseEntity(
            T payload,
            HttpStatus httpStatus,
            String message
    ) {
        this.status = httpStatus.value();
        this.statusDescription = httpStatus.getReasonPhrase();
        this.message = message;
        this.payload = payload;

        return new ResponseEntity<>(this, httpStatus);
    }
}
