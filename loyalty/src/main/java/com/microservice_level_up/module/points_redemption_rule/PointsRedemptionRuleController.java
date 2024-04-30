package com.microservice_level_up.module.points_redemption_rule;

import com.microservice_level_up.module.points_redemption_rule.dto.NewPointsRedemptionRule;
import com.microservice_level_up.module.points_redemption_rule.dto.PointsRedemptionRuleResponse;
import com.microservice_level_up.module.points_redemption_rule.dto.UpdatePointsRedemptionRule;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Redemption Points Rule")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pointsRedemptionRule")
public class PointsRedemptionRuleController {

    private final IPointsRedemptionRuleService service;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_REDEMPTION_POINTS_RULE')")
    @Operation(summary = "Get all rules paged")
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
    @PreAuthorize("hasAuthority('CREATE_REDEMPTION_POINTS_RULE')")
    @Operation(summary = "Create a new rule")
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
    @PreAuthorize("hasAuthority('UPDATE_REDEMPTION_POINTS_RULE')")
    @Operation(summary = "Update an existing rule")
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
    @PreAuthorize("hasAuthority('UPDATE_REDEMPTION_POINTS_RULE')")
    @Operation(summary = "Activate a specific rule")
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
