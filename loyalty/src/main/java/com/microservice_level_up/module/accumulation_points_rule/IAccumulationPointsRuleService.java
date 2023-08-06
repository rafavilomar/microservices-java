package com.microservice_level_up.module.accumulation_points_rule;

import java.util.Optional;

public interface IAccumulationPointsRuleService {
    Optional<AccumulationPointsRule> getActive();
}
