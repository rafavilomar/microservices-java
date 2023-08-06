package com.microservice_level_up.module.points_redemption_rule;

import java.util.Optional;

public interface IPointsRedemptionRuleService {
    Optional<PointsRedemptionRule> getActive();
}
