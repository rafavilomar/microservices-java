package com.microservice_level_up.module.points_redemption_rule;

import com.microservice_level_up.module.points_redemption_rule.dto.NewPointsRedemptionRule;
import com.microservice_level_up.module.points_redemption_rule.dto.PointsRedemptionRuleResponse;
import com.microservice_level_up.module.points_redemption_rule.dto.UpdatePointsRedemptionRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public record PointsRedemptionRuleService(
        PointsRedemptionRuleRepository repository
) implements IPointsRedemptionRuleService {

    @Override
    public long create(NewPointsRedemptionRule newPointsRedemptionRule) {
        log.info("Create points redemption rule {}", newPointsRedemptionRule);

        return repository.save(PointsRedemptionRule.builder()
                .pointsToRedeem(newPointsRedemptionRule.pointsToRedeem())
                .dollar(newPointsRedemptionRule.dollar())
                .status(false)
                .createdAt(LocalDateTime.now())
                .build()).getId();
    }

    @Override
    public long update(UpdatePointsRedemptionRule updatePointsRedemptionRule) {
        log.info("Update points redemption rule {}", updatePointsRedemptionRule);
        PointsRedemptionRule pointsRedemptionRule = getById(updatePointsRedemptionRule.id());

        pointsRedemptionRule.setPointsToRedeem(updatePointsRedemptionRule.pointsToRedeem());
        pointsRedemptionRule.setDollar(updatePointsRedemptionRule.dollar());
        pointsRedemptionRule.setUpdatedAt(LocalDateTime.now());

        return repository.save(pointsRedemptionRule).getId();
    }

    @Override
    public void activate(long idAccumulationPointsRule) {
        log.info("Activate points redemption rule {}", idAccumulationPointsRule);
        PointsRedemptionRule ruleToActivate = getById(idAccumulationPointsRule);
        Optional<PointsRedemptionRule> currentRuleActive = getActive();

        if (currentRuleActive.isPresent()) {
            currentRuleActive.get().setStatus(false);
            currentRuleActive.get().setUpdatedAt(LocalDateTime.now());
            repository.save(currentRuleActive.get());
        }

        ruleToActivate.setStatus(true);
        ruleToActivate.setUpdatedAt(LocalDateTime.now());
        repository.save(ruleToActivate);
    }

    @Override
    public Page<PointsRedemptionRuleResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(rule -> PointsRedemptionRuleResponse.builder()
                .id(rule.getId())
                .pointsToRedeem(rule.getPointsToRedeem())
                .dollar(rule.getDollar())
                .status(rule.isStatus())
                .build());
    }

    @Override
    public PointsRedemptionRule getById(long idAccumulationPointsRule) {
        return repository.findById(idAccumulationPointsRule)
                .orElseThrow(() -> new EntityNotFoundException("Accumulation Points Rule not found"));
    }

    @Override
    public Optional<PointsRedemptionRule> getActive() {
        return repository.findByStatus(true);
    }
}
