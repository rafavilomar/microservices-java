package com.microservice_level_up.module.point;

import com.microservice_level_up.enums.MovementType;
import com.microservice_level_up.error.not_active_points_redemption_rule.NotActivePointsRedemptionRuleException;
import com.microservice_level_up.error.not_enough_points.NotEnoughPointsException;
import com.microservice_level_up.module.accumulation_points_rule.AccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.IAccumulationPointsRuleService;
import com.microservice_level_up.module.point.dto.PurchaseRequest;
import com.microservice_level_up.module.point.dto.SimpleLotPoints;
import com.microservice_level_up.module.point.entities.LotPoints;
import com.microservice_level_up.module.point.entities.PointsMovementHistory;
import com.microservice_level_up.module.point.repository.LotPointsRepository;
import com.microservice_level_up.module.point.repository.PointsMovementHistoryRepository;
import com.microservice_level_up.module.points_redemption_rule.IPointsRedemptionRuleService;
import com.microservice_level_up.module.points_redemption_rule.PointsRedemptionRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsServiceTest {

    @InjectMocks
    private PointsService underTest;

    @Mock
    private LotPointsRepository lotPointsRepository;
    @Mock
    private PointsMovementHistoryRepository pointsMovementHistoryRepository;
    @Mock
    private IAccumulationPointsRuleService accumulationPointsRuleService;
    @Mock
    private IPointsRedemptionRuleService pointsRedemptionRuleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void accumulatePoints_NoLotPoints() {
        PurchaseRequest purchaseRequest = new PurchaseRequest(1, 10, 10, LocalDateTime.now(), "UUID");
        AccumulationPointsRule accumulationPointsRule = accumulationPointsRuleTemplate(1);
        LotPoints lotPoints = lotPointsTemplate(2);
        lotPoints.setPoints(0);
        PointsMovementHistory pointsMovement = PointsMovementHistory.builder()
                .points(100)
                .dollar(purchaseRequest.dollar())
                .movementDate(purchaseRequest.movementDate())
                .type(MovementType.ACCUMULATION)
                .lotPoints(lotPoints)
                .invoiceUuid(purchaseRequest.invoiceUuid())
                .build();

        when(accumulationPointsRuleService.getActive()).thenReturn(Optional.of(accumulationPointsRule));
        when(lotPointsRepository.findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true))
                .thenReturn(Optional.empty());
        when(lotPointsRepository.save(any())).thenReturn(lotPoints);

        underTest.accumulatePoints(purchaseRequest);

        assertEquals(100, lotPoints.getPoints());

        verify(accumulationPointsRuleService, times(1)).getActive();
        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true);
        verify(pointsMovementHistoryRepository, times(1)).save(pointsMovement);
        verify(lotPointsRepository, times(2)).save(any(LotPoints.class));
        verifyNoMoreInteractions(accumulationPointsRuleService, lotPointsRepository, pointsMovementHistoryRepository);
        verifyNoInteractions(pointsRedemptionRuleService);
    }

    @Test
    void accumulatePoints_ActiveAccumulationRule() {
        PurchaseRequest purchaseRequest = new PurchaseRequest(1, 10, 10, LocalDateTime.now(), "UUID");
        AccumulationPointsRule accumulationPointsRule = accumulationPointsRuleTemplate(1);
        LotPoints lotPoints = lotPointsTemplate(1);
        PointsMovementHistory pointsMovement = PointsMovementHistory.builder()
                .points(100)
                .dollar(purchaseRequest.dollar())
                .movementDate(purchaseRequest.movementDate())
                .type(MovementType.ACCUMULATION)
                .lotPoints(lotPoints)
                .invoiceUuid(purchaseRequest.invoiceUuid())
                .build();

        when(accumulationPointsRuleService.getActive()).thenReturn(Optional.of(accumulationPointsRule));
        when(lotPointsRepository.findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true))
                .thenReturn(Optional.of(lotPoints));

        underTest.accumulatePoints(purchaseRequest);

        assertEquals(200, lotPoints.getPoints());

        verify(accumulationPointsRuleService, times(1)).getActive();
        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true);
        verify(pointsMovementHistoryRepository, times(1)).save(pointsMovement);
        verify(lotPointsRepository, times(1)).save(lotPoints);
        verifyNoMoreInteractions(accumulationPointsRuleService, lotPointsRepository, pointsMovementHistoryRepository);
        verifyNoInteractions(pointsRedemptionRuleService);
    }

    @Test
    void accumulatePoints_NoActiveAccumulationRule() {
        PurchaseRequest purchaseRequest = new PurchaseRequest(1, 10, 10, LocalDateTime.now(), "UUID");
        LotPoints lotPoints = lotPointsTemplate(1);
        PointsMovementHistory pointsMovement = PointsMovementHistory.builder()
                .points(0)
                .dollar(purchaseRequest.dollar())
                .movementDate(purchaseRequest.movementDate())
                .type(MovementType.ACCUMULATION)
                .lotPoints(lotPoints)
                .invoiceUuid(purchaseRequest.invoiceUuid())
                .build();

        when(accumulationPointsRuleService.getActive()).thenReturn(Optional.empty());
        when(lotPointsRepository.findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true))
                .thenReturn(Optional.of(lotPoints));

        underTest.accumulatePoints(purchaseRequest);

        verify(accumulationPointsRuleService, times(1)).getActive();
        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true);
        verify(pointsMovementHistoryRepository, times(1)).save(pointsMovement);
        verifyNoMoreInteractions(accumulationPointsRuleService, lotPointsRepository, pointsMovementHistoryRepository);
        verifyNoInteractions(pointsRedemptionRuleService);
    }

    @Test
    void redeemPoints_NotEnoughPoints() {
        PurchaseRequest purchaseRequest = new PurchaseRequest(1, 10, 500, LocalDateTime.now(), "UUID");
        PointsRedemptionRule pointsRedemptionRule = pointsRedemptionRuleTemplate(1);
        LotPoints lotPoints = lotPointsTemplate(1);

        when(pointsRedemptionRuleService.getActive()).thenReturn(Optional.of(pointsRedemptionRule));
        when(lotPointsRepository.findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true))
                .thenReturn(Optional.of(lotPoints));

        assertThrows(NotEnoughPointsException.class, () -> underTest.redeemPoints(purchaseRequest));

        verify(pointsRedemptionRuleService, times(1)).getActive();
        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true);
        verifyNoMoreInteractions(pointsRedemptionRuleService, lotPointsRepository);
        verifyNoInteractions(accumulationPointsRuleService, pointsMovementHistoryRepository);
    }

    @Test
    void redeemPoints_NotActivePointsRedemptionRule() {
        PurchaseRequest purchaseRequest = new PurchaseRequest(1, 10, 10, LocalDateTime.now(), "UUID");
        LotPoints lotPoints = lotPointsTemplate(1);

        when(pointsRedemptionRuleService.getActive()).thenReturn(Optional.empty());
        when(lotPointsRepository.findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true))
                .thenReturn(Optional.of(lotPoints));

        assertThrows(NotActivePointsRedemptionRuleException.class, () -> underTest.redeemPoints(purchaseRequest));

        verify(pointsRedemptionRuleService, times(1)).getActive();
        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true);
        verifyNoMoreInteractions(pointsRedemptionRuleService, lotPointsRepository);
        verifyNoInteractions(accumulationPointsRuleService, pointsMovementHistoryRepository);
    }

    @Test
    void redeemPoints_ShouldBeOk() {
        PurchaseRequest purchaseRequest = new PurchaseRequest(1, 10, 10, LocalDateTime.now(), "UUID");
        PointsRedemptionRule pointsRedemptionRule = pointsRedemptionRuleTemplate(1);
        LotPoints lotPoints = lotPointsTemplate(1);
        PointsMovementHistory pointsMovement = PointsMovementHistory.builder()
                .points(purchaseRequest.points())
                .dollar(0.5)
                .movementDate(purchaseRequest.movementDate())
                .type(MovementType.REDEMPTION)
                .lotPoints(lotPoints)
                .invoiceUuid(purchaseRequest.invoiceUuid())
                .build();

        when(pointsRedemptionRuleService.getActive()).thenReturn(Optional.of(pointsRedemptionRule));
        when(lotPointsRepository.findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true))
                .thenReturn(Optional.of(lotPoints));

        underTest.redeemPoints(purchaseRequest);

        assertEquals(90, lotPoints.getPoints());

        verify(pointsRedemptionRuleService, times(1)).getActive();
        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(purchaseRequest.idCustomer(), true);
        verify(pointsMovementHistoryRepository, times(1)).save(pointsMovement);
        verify(lotPointsRepository, times(1)).save(lotPoints);
        verifyNoMoreInteractions(pointsRedemptionRuleService, lotPointsRepository, pointsMovementHistoryRepository);
        verifyNoInteractions(accumulationPointsRuleService);
    }

    @Test
    void getPointsInfo() {
        long idCustomer = 1;
        LotPoints lotPoints = lotPointsTemplate(1);

        when(lotPointsRepository.findByIdCustomerAndStatus(idCustomer, true)).thenReturn(Optional.of(lotPoints));

        Optional<SimpleLotPoints> actualResponse = underTest.getPointsInfo(idCustomer);

        assertTrue(actualResponse.isPresent());
        assertAll("Get points information",
                () -> assertEquals(lotPoints.getPoints(), actualResponse.get().points()),
                () -> assertEquals(lotPoints.getExpirationDate(), actualResponse.get().expirationDate()));

        verify(lotPointsRepository, times(1)).findByIdCustomerAndStatus(idCustomer, true);
        verifyNoMoreInteractions(lotPointsRepository);
        verifyNoInteractions(pointsMovementHistoryRepository, accumulationPointsRuleService, pointsRedemptionRuleService);
    }

    private LotPoints lotPointsTemplate(long id){
        return LotPoints.builder()
                .id(id)
                .points(100)
                .expirationDate(LocalDate.now().plusMonths(3))
                .build();
    }

    private AccumulationPointsRule accumulationPointsRuleTemplate(long id){
        return AccumulationPointsRule.builder()
                .id(id)
                .pointsToEarn(10)
                .dollar(1)
                .status(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private PointsRedemptionRule pointsRedemptionRuleTemplate(long id){
        return PointsRedemptionRule.builder()
                .id(id)
                .pointsToRedeem(10)
                .dollar(0.5)
                .status(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}