package com.microservice_level_up.module.points_redemption_rule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointsRedemptionRuleRepository extends JpaRepository<PointsRedemptionRule, Long> {

    Optional<PointsRedemptionRule> findByStatus(boolean status);
}
