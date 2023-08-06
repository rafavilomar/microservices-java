package com.microservice_level_up.module.accumulation_points_rule.dto;

public record UpdateAccumulationPointsRule(
        long id,
        int pointsToEarn,
        double dollar
) {
}
