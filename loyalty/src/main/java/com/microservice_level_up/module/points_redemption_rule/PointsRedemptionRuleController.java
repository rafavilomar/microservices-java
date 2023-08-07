package com.microservice_level_up.module.points_redemption_rule;

import com.microservice_level_up.module.points_redemption_rule.dto.NewPointsRedemptionRule;
import com.microservice_level_up.module.points_redemption_rule.dto.PointsRedemptionRuleResponse;
import com.microservice_level_up.module.points_redemption_rule.dto.UpdatePointsRedemptionRule;
import com.microservice_level_up.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pointsRedemptionRule")
public record PointsRedemptionRuleController(IPointsRedemptionRuleService service) {

    @GetMapping
    public ResponseEntity<BaseResponse<Page<PointsRedemptionRuleResponse>>> getAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<PointsRedemptionRuleResponse> payload = service.getAll(pageable);

        BaseResponse<Page<PointsRedemptionRuleResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Points Redemption Rules found"
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> create(@RequestBody NewPointsRedemptionRule request) {
        long payload = service.create(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Points Redemption Rule created"
        );
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Long>> update(@RequestBody UpdatePointsRedemptionRule request) {
        long payload = service.update(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Points Redemption Rule updated"
        );
    }

    @PutMapping("/activate/{idAccumulationPointsRule}")
    public ResponseEntity<BaseResponse<Void>> activate(@PathVariable long idAccumulationPointsRule) {
        service.activate(idAccumulationPointsRule);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.OK,
                "Points Redemption Rule activated"
        );
    }
}
