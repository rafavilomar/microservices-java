package com.microservice_level_up.module.accumulation_points_rule;

import com.microservice_level_up.module.accumulation_points_rule.dto.NewAccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.dto.UpdateAccumulationPointsRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccumulationPointsRuleServiceTest {

    @InjectMocks
    private AccumulationPointsRuleService underTest;

    @Mock
    private AccumulationPointsRuleRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldBeOk(){
        AccumulationPointsRule rule = AccumulationPointsRule.builder().id(1L).build();
        NewAccumulationPointsRule newRule = new NewAccumulationPointsRule(10, 1);

        when(repository.save(any(AccumulationPointsRule.class))).thenReturn(rule);

        underTest.create(newRule);

        verify(repository, times(1)).save(any(AccumulationPointsRule.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_ShouldBeOk(){
        AccumulationPointsRule rule = AccumulationPointsRule.builder().id(1L).build();
        UpdateAccumulationPointsRule ruleUpdated = new UpdateAccumulationPointsRule(1, 10, 1);

        when(repository.findById(ruleUpdated.id())).thenReturn(Optional.of(rule));
        when(repository.save(any(AccumulationPointsRule.class))).thenReturn(rule);
        
        underTest.update(ruleUpdated);

        assertAll(
                "Update accumulation point rules: rule updated",
                () -> assertEquals(ruleUpdated.pointsToEarn(), rule.getPointsToEarn()),
                () -> assertEquals(ruleUpdated.dollar(), rule.getDollar()),
                () -> assertNotNull(rule.getUpdatedAt()));

        verify(repository, times(1)).findById(ruleUpdated.id());
        verify(repository, times(1)).save(rule);
        verifyNoMoreInteractions(repository);
    }
}