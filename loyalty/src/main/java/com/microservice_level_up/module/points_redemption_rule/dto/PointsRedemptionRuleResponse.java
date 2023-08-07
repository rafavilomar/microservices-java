package com.microservice_level_up.module.points_redemption_rule.dto;

import lombok.Builder;

@Builder
public record PointsRedemptionRuleResponse(
        Long id,
        int pointsToRedeem,

        double dollar,

        boolean status
) {
}
