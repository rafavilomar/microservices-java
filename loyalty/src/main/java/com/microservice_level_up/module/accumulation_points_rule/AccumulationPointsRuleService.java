package com.microservice_level_up.module.accumulation_points_rule;

import com.microservice_level_up.module.accumulation_points_rule.dto.NewAccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.dto.UpdateAccumulationPointsRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccumulationPointsRuleService implements IAccumulationPointsRuleService {

    private final AccumulationPointsRuleRepository repository;

    @Override
    public void create(NewAccumulationPointsRule newAccumulationPointsRule) {
        log.info("Create accumulation points rule {}", newAccumulationPointsRule);

        repository.save(AccumulationPointsRule.builder()
                .pointsToEarn(newAccumulationPointsRule.pointsToEarn())
                .dollar(newAccumulationPointsRule.dollar())
                .status(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public void update(UpdateAccumulationPointsRule updateAccumulationPointsRule) {
        log.info("Update accumulation points rule {}", updateAccumulationPointsRule);
        AccumulationPointsRule accumulationPointsRule = getById(updateAccumulationPointsRule.id());

        accumulationPointsRule.setPointsToEarn(updateAccumulationPointsRule.pointsToEarn());
        accumulationPointsRule.setDollar(updateAccumulationPointsRule.dollar());
        accumulationPointsRule.setUpdatedAt(LocalDateTime.now());

        repository.save(accumulationPointsRule);
    }

    @Override
    public void activate(long idAccumulationPointsRule) {
        log.info("Activate accumulation points rule {}", idAccumulationPointsRule);
        AccumulationPointsRule ruleToActivate = repository.getById(idAccumulationPointsRule);
        Optional<AccumulationPointsRule> currentRuleActive = repository.findByStatus(true);

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
    public Page<AccumulationPointsRule> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public AccumulationPointsRule getById(long idAccumulationPointsRule) {
        return repository.findById(idAccumulationPointsRule)
                .orElseThrow(() -> new EntityNotFoundException("Accumulation Points Rule not found"));
    }

    @Override
    public Optional<AccumulationPointsRule> getActive() {
        return Optional.empty();
    }
}
