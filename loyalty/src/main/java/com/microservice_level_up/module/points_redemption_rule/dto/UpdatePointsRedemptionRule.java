package com.microservice_level_up.module.points_redemption_rule.dto;

public record UpdatePointsRedemptionRule(
        long id,
        int pointsToRedeem,
        double dollar
) {
}
