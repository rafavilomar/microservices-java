package com.microservice_level_up.module.accumulation_points_rule;

import com.microservice_level_up.module.accumulation_points_rule.dto.AccumulationPointsRuleResponse;
import com.microservice_level_up.module.accumulation_points_rule.dto.NewAccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.dto.UpdateAccumulationPointsRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public record AccumulationPointsRuleService(
        AccumulationPointsRuleRepository repository
) implements IAccumulationPointsRuleService {

    @Override
    public long create(NewAccumulationPointsRule newAccumulationPointsRule) {
        log.info("Create accumulation points rule {}", newAccumulationPointsRule);

        return repository.save(AccumulationPointsRule.builder()
                .pointsToEarn(newAccumulationPointsRule.pointsToEarn())
                .dollar(newAccumulationPointsRule.dollar())
                .status(false)
                .createdAt(LocalDateTime.now())
                .build()).getId();
    }

    @Override
    public long update(UpdateAccumulationPointsRule updateAccumulationPointsRule) {
        log.info("Update accumulation points rule {}", updateAccumulationPointsRule);
        AccumulationPointsRule accumulationPointsRule = getById(updateAccumulationPointsRule.id());

        accumulationPointsRule.setPointsToEarn(updateAccumulationPointsRule.pointsToEarn());
        accumulationPointsRule.setDollar(updateAccumulationPointsRule.dollar());
        accumulationPointsRule.setUpdatedAt(LocalDateTime.now());

        return repository.save(accumulationPointsRule).getId();
    }

    @Override
    public void activate(long idAccumulationPointsRule) {
        log.info("Activate accumulation points rule {}", idAccumulationPointsRule);
        AccumulationPointsRule ruleToActivate = repository.getById(idAccumulationPointsRule);
        Optional<AccumulationPointsRule> currentRuleActive = getActive();

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
    public Page<AccumulationPointsRuleResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(rule -> AccumulationPointsRuleResponse.builder()
                .id(rule.getId())
                .pointsToEarn(rule.getPointsToEarn())
                .dollar(rule.getDollar())
                .status(rule.isStatus())
                .build());
    }

    @Override
    public AccumulationPointsRule getById(long idAccumulationPointsRule) {
        return repository.findById(idAccumulationPointsRule)
                .orElseThrow(() -> new EntityNotFoundException("Accumulation Points Rule not found"));
    }

    @Override
    public Optional<AccumulationPointsRule> getActive() {
        return repository.findByStatus(true);
    }
}
