package com.microservice_level_up.module.points_redemption_rule.dto;

public record NewPointsRedemptionRule(
        int pointsToRedeem,
        double dollar
) {
}
