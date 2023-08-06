package com.microservice_level_up.module.points_redemption_rule;

import com.microservice_level_up.module.points_redemption_rule.dto.NewPointsRedemptionRule;
import com.microservice_level_up.module.points_redemption_rule.dto.UpdatePointsRedemptionRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IPointsRedemptionRuleService {
    void create(NewPointsRedemptionRule newPointsRedemptionRule);

    void update(UpdatePointsRedemptionRule updatePointsRedemptionRule);

    void activate(long idPointsRedemptionRule);

    Page<PointsRedemptionRule> getAll(Pageable pageable);

    PointsRedemptionRule getById(long idPointsRedemptionRule);

    Optional<PointsRedemptionRule> getActive();
}
