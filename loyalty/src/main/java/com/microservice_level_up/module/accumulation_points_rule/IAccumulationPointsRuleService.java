package com.microservice_level_up.module.accumulation_points_rule;

import com.microservice_level_up.module.accumulation_points_rule.dto.AccumulationPointsRuleResponse;
import com.microservice_level_up.module.accumulation_points_rule.dto.NewAccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.dto.UpdateAccumulationPointsRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IAccumulationPointsRuleService {
    long create(NewAccumulationPointsRule newAccumulationPointsRule);

    long update(UpdateAccumulationPointsRule updateAccumulationPointsRule);

    void activate(long idAccumulationPointsRule);

    Page<AccumulationPointsRuleResponse> getAll(Pageable pageable);

    AccumulationPointsRule getById(long idAccumulationPointsRule);

    Optional<AccumulationPointsRule> getActive();
}
