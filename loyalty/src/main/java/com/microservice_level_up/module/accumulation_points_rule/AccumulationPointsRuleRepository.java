package com.microservice_level_up.module.accumulation_points_rule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccumulationPointsRuleRepository extends JpaRepository<AccumulationPointsRule, Long> {

    Optional<AccumulationPointsRule> findByStatus(boolean status);
}
