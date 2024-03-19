package com.microservice_level_up.dto;


import com.microservice_level_up.enums.MovementType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record PointsResponse(
        int points,
        double dollar,

        @Enumerated(EnumType.STRING)
        MovementType type
) {
}
