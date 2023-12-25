package com.customer.kafka.events;

import java.time.LocalDateTime;

public record Event<T>(
        String id,
        LocalDateTime date,
        EventType type,
        T data
) {
}
