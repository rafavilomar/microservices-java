package com.microservice_level_up.module.accumulation_points_rule.dto;

import lombok.Builder;

@Builder
public record AccumulationPointsRuleResponse(
        Long id,
        int pointsToEarn,

        double dollar,

        boolean status
) {
}
