package com.microservice_level_up.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class BaseResponse<T> {

    private LocalDateTime timestamp;
    private Integer status;
    private String statusDescription;
    private String message;
    private T payload;

    public ResponseEntity<BaseResponse<T>> buildResponseEntity(
            T payload,
            HttpStatus httpStatus,
            String message
    ) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.statusDescription = httpStatus.getReasonPhrase();
        this.message = message;
        this.payload = payload;

        return new ResponseEntity<>(this, httpStatus);
    }
}
