package com.microservice_level_up.module.point;

import com.microservice_level_up.dto.PointsResponse;
import com.microservice_level_up.enums.MovementType;
import com.microservice_level_up.error.http_exeption.NotActivePointsRedemptionRuleException;
import com.microservice_level_up.error.http_exeption.NotEnoughPointsException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public record PointsService(
        LotPointsRepository lotPointsRepository,
        PointsMovementHistoryRepository pointsMovementHistoryRepository,
        IAccumulationPointsRuleService accumulationPointsRuleService,
        IPointsRedemptionRuleService pointsRedemptionRuleService
) implements IPointsService {

    @Override
    public PointsResponse accumulatePoints(PurchaseRequest purchaseRequest) {
        int pointsAccumulated = 0;
        Optional<AccumulationPointsRule> accumulationPointsRule = accumulationPointsRuleService.getActive();
        LotPoints lotPoints = getOrCreateActiveLotPointsByCustomer(purchaseRequest.idCustomer());

        if (accumulationPointsRule.isPresent()) {

            double dollarToEvaluate = purchaseRequest.dollar() / accumulationPointsRule.get().getDollar();
            pointsAccumulated = accumulationPointsRule.get().getPointsToEarn() * (int) dollarToEvaluate;

            pointsMovementHistoryRepository.save(PointsMovementHistory.builder()
                    .points(pointsAccumulated)
                    .dollar(purchaseRequest.dollar())
                    .movementDate(purchaseRequest.movementDate())
                    .type(MovementType.ACCUMULATION)
                    .lotPoints(lotPoints)
                    .invoiceUuid(purchaseRequest.invoiceUuid())
                    .build());

            lotPoints.setPoints(lotPoints.getPoints() + pointsAccumulated);
            lotPoints.setUpdatedAt(purchaseRequest.movementDate());
            lotPointsRepository.save(lotPoints);

        } else {
            log.warn("No active accumulation points rule found");
            pointsMovementHistoryRepository.save(PointsMovementHistory.builder()
                    .points(pointsAccumulated)
                    .dollar(purchaseRequest.dollar())
                    .movementDate(purchaseRequest.movementDate())
                    .type(MovementType.ACCUMULATION)
                    .lotPoints(lotPoints)
                    .invoiceUuid(purchaseRequest.invoiceUuid())
                    .build());
        }

        return new PointsResponse(pointsAccumulated, purchaseRequest.dollar(), MovementType.ACCUMULATION);
    }

    @Override
    public PointsResponse redeemPoints(PurchaseRequest purchaseRequest) {
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
                .invoiceUuid(purchaseRequest.invoiceUuid())
                .build());

        lotPoints.setPoints(lotPoints.getPoints() - purchaseRequest.points());
        lotPoints.setUpdatedAt(purchaseRequest.movementDate());
        lotPointsRepository.save(lotPoints);

        return new PointsResponse(purchaseRequest.points(), dollarEquivalency, MovementType.REDEMPTION);
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
