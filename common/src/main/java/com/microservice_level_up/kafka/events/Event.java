package com.microservice_level_up.kafka.events;

import java.time.LocalDateTime;

public record Event<T>(
        String id,
        LocalDateTime date,
        EventType type,
        T data
) {
}
