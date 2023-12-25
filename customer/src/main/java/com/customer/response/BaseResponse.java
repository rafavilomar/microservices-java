package com.customer.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class BaseResponse<T> {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
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
