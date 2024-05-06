package com.microservice_level_up.module.accumulation_points_rule.dto;

public record NewAccumulationPointsRule(
        int pointsToEarn,
        double dollar
) {
}
