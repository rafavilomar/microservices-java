package com.microservice_level_up.module.point.dto;

import java.time.LocalDate;

public record SimpleLotPoints(
        int points,
        LocalDate expirationDate
) {
}
