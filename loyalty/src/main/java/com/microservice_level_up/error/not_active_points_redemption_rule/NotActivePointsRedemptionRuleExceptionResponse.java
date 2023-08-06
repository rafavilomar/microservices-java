package com.microservice_level_up.error.not_active_points_redemption_rule;

import java.time.LocalDateTime;

public record NotActivePointsRedemptionRuleExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String statusDescription,
        String error) {
}
