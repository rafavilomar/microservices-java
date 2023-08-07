package com.microservice_level_up.module.points_redemption_rule;

import com.microservice_level_up.module.points_redemption_rule.dto.NewPointsRedemptionRule;
import com.microservice_level_up.module.points_redemption_rule.dto.PointsRedemptionRuleResponse;
import com.microservice_level_up.module.points_redemption_rule.dto.UpdatePointsRedemptionRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IPointsRedemptionRuleService {
    long create(NewPointsRedemptionRule newPointsRedemptionRule);

    long update(UpdatePointsRedemptionRule updatePointsRedemptionRule);

    void activate(long idPointsRedemptionRule);

    Page<PointsRedemptionRuleResponse> getAll(Pageable pageable);

    PointsRedemptionRule getById(long idPointsRedemptionRule);

    Optional<PointsRedemptionRule> getActive();
}
