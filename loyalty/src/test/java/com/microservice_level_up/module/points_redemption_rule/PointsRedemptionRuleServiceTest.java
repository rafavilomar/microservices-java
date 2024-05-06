package com.microservice_level_up.module.points_redemption_rule;

import com.microservice_level_up.module.points_redemption_rule.dto.NewPointsRedemptionRule;
import com.microservice_level_up.module.points_redemption_rule.dto.PointsRedemptionRuleResponse;
import com.microservice_level_up.module.points_redemption_rule.dto.UpdatePointsRedemptionRule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PointsRedemptionRuleServiceTest {

    @InjectMocks
    private PointsRedemptionRuleService underTest;

    @Mock
    private PointsRedemptionRuleRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create() {
        long idRuleCreated = 1;
        NewPointsRedemptionRule newRule = new NewPointsRedemptionRule(10, 5);

        when(repository.save(any(PointsRedemptionRule.class))).thenReturn(getRule(idRuleCreated));

        assertEquals(idRuleCreated, underTest.create(newRule));

        verify(repository, times(1)).save(any(PointsRedemptionRule.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update() {
        PointsRedemptionRule rule = getRule(1L);
        UpdatePointsRedemptionRule ruleToUpdate = new UpdatePointsRedemptionRule(rule.getId(), 10, 1);

        when(repository.findById(rule.getId())).thenReturn(Optional.of(rule));
        when(repository.save(any(PointsRedemptionRule.class))).thenReturn(rule);

        underTest.update(ruleToUpdate);

        assertAll("Update a points redemption rule",
                () -> assertEquals(ruleToUpdate.pointsToRedeem(), rule.getPointsToRedeem()),
                () -> assertEquals(ruleToUpdate.dollar(), rule.getDollar()),
                () -> assertNotNull(rule.getUpdatedAt()),
                () -> assertEquals(ruleToUpdate.id(), rule.getId()));

        verify(repository, times(1)).findById(ruleToUpdate.id());
        verify(repository, times(1)).save(any(PointsRedemptionRule.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void activate() {
        long idAccumulationPointsRule = 1;
        PointsRedemptionRule ruleToActivate = getRule(idAccumulationPointsRule);
        ruleToActivate.setStatus(false);
        ruleToActivate.setPointsToRedeem(10);
        PointsRedemptionRule currentActiveRule = getRule(2);

        when(repository.findById(idAccumulationPointsRule)).thenReturn(Optional.of(ruleToActivate));
        when(repository.findByStatus(true)).thenReturn(Optional.of(currentActiveRule));

        underTest.activate(idAccumulationPointsRule);

        assertAll("Activate another rule",
                () -> assertTrue(ruleToActivate.isStatus()),
                () -> assertFalse(currentActiveRule.isStatus()));

        verify(repository, times(1)).findById(idAccumulationPointsRule);
        verify(repository, times(1)).findByStatus(true);
        verify(repository, times(1)).save(currentActiveRule);
        verify(repository, times(1)).save(ruleToActivate);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 1);
        List<PointsRedemptionRule> rules = List.of(
                getRule(1L),
                getRule(2L)
        );
        Page<PointsRedemptionRule> page = new PageImpl<>(rules, pageable, rules.size());

        when(repository.findAll(pageable)).thenReturn(page);

        List<PointsRedemptionRuleResponse> rulesResponse = List.of(
                getRuleResponse(rules.get(0)),
                getRuleResponse(rules.get(1))
        );
        Page<PointsRedemptionRuleResponse> expectedResponse = new PageImpl<>(rulesResponse, pageable, rulesResponse.size());

        Page<PointsRedemptionRuleResponse> actualResponse = underTest.getAll(pageable);

        assertEquals(expectedResponse, actualResponse);

        verify(repository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getById_NotFound() {
        long idAccumulationPointsRule = 1;

        when(repository.findById(idAccumulationPointsRule)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> underTest.getById(idAccumulationPointsRule));

        verify(repository, times(1)).findById(idAccumulationPointsRule);
        verifyNoMoreInteractions(repository);
    }

    private PointsRedemptionRule getRule(long idAccumulationPointsRule){
        return PointsRedemptionRule.builder()
                .id(idAccumulationPointsRule)
                .pointsToRedeem(20)
                .dollar(2)
                .status(true)
                .build();
    }

    private PointsRedemptionRuleResponse getRuleResponse(PointsRedemptionRule rule){
        return PointsRedemptionRuleResponse.builder()
                .id(rule.getId())
                .pointsToRedeem(rule.getPointsToRedeem())
                .dollar(rule.getDollar())
                .status(rule.isStatus())
                .build();
    }
}