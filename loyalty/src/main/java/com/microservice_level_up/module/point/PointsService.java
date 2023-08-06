package com.microservice_level_up.module.point;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsService implements IPointsService {

    private final LotPointsRepository lotPointsRepository;
    private final PointsMovementHistoryRepository pointsMovementHistoryRepository;
    private final IAccumulationPointsRuleService accumulationPointsRuleService;
    private final IPointsRedemptionRuleService pointsRedemptionRuleService;

    @Override
    public void accumulatePoints(PurchaseRequest purchaseRequest) {
        Optional<AccumulationPointsRule> accumulationPointsRule = accumulationPointsRuleService.getActive();
        LotPoints lotPoints = getOrCreateActiveLotPointsByCustomer(purchaseRequest.idCustomer());

        if (accumulationPointsRule.isPresent()) {

            double dollarToEvaluate = purchaseRequest.dollar() / accumulationPointsRule.get().getDollar();
            int pointsAccumulated = accumulationPointsRule.get().getPointsToEarn() * (int) dollarToEvaluate;

            pointsMovementHistoryRepository.save(PointsMovementHistory.builder()
                    .points(pointsAccumulated)
                    .dollar(purchaseRequest.dollar())
                    .movementDate(purchaseRequest.movementDate())
                    .type(MovementType.ACCUMULATION)
                    .lotPoints(lotPoints)
                    .build());

            lotPoints.setPoints(lotPoints.getPoints() + pointsAccumulated);
            lotPoints.setUpdatedAt(purchaseRequest.movementDate());
            lotPointsRepository.save(lotPoints);

        } else {
            log.warn("No active accumulation points rule found");
            pointsMovementHistoryRepository.save(PointsMovementHistory.builder()
                    .points(0)
                    .dollar(purchaseRequest.dollar())
                    .movementDate(purchaseRequest.movementDate())
                    .type(MovementType.ACCUMULATION)
                    .lotPoints(lotPoints)
                    .build());
        }
    }

    @Override
    public void redeemPoints(PurchaseRequest purchaseRequest) {
        Optional<PointsRedemptionRule> pointsRedemptionRule = pointsRedemptionRuleService.getActive();
        LotPoints lotPoints = getOrCreateActiveLotPointsByCustomer(purchaseRequest.idCustomer());

        if (purchaseRequest.points() > lotPoints.getPoints())
            throw new NotEnoughPointsException("Not enough points to redeem " + purchaseRequest.points());

        if (pointsRedemptionRule.isEmpty())
            throw new NotActivePointsRedemptionRuleException("Not active Points Redemption Rule found");

        int pointsToEvaluate = purchaseRequest.points() / pointsRedemptionRule.get().getPointsToRedeem();
        double dollarEquivalency = pointsToEvaluate * pointsRedemptionRule.get().getDollar();

        pointsMovementHistoryRepository.save(PointsMovementHistory.builder()
                .points(purchaseRequest.points())
                .dollar(dollarEquivalency)
                .movementDate(purchaseRequest.movementDate())
                .type(MovementType.REDEMPTION)
                .lotPoints(lotPoints)
                .build());

        lotPoints.setPoints(lotPoints.getPoints() - purchaseRequest.points());
        lotPoints.setUpdatedAt(purchaseRequest.movementDate());
        lotPointsRepository.save(lotPoints);
    }

    @Override
    public Optional<SimpleLotPoints> getPointsInfo(long idCustomer) {
        return lotPointsRepository.findByIdCustomerAndStatus(idCustomer, true)
                .map(lot -> new SimpleLotPoints(lot.getPoints(), lot.getExpirationDate()));
    }

    private LotPoints getOrCreateActiveLotPointsByCustomer(long idCustomer) {
        Optional<LotPoints> lotPoints = lotPointsRepository.findByIdCustomerAndStatus(idCustomer, true);

        if (lotPoints.isEmpty()) {
            lotPoints = Optional.of(lotPointsRepository.save(LotPoints.builder()
                    .createdAt(LocalDateTime.now())
                    .status(true)
                    .idCustomer(idCustomer)
                    .points(0)
                    .expirationDate(LocalDate.now().withDayOfYear(365))
                    .build()));
        }

        return lotPoints.get();
    }
}
