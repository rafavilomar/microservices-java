package com.microservice_level_up.module.accumulation_points_rule;

import com.microservice_level_up.module.accumulation_points_rule.dto.AccumulationPointsRuleResponse;
import com.microservice_level_up.module.accumulation_points_rule.dto.NewAccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.dto.UpdateAccumulationPointsRule;
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
@Tag(name = "Accumulation Points Rule")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accumulationPointsRule")
public class AccumulationPointsRuleController {

    private final IAccumulationPointsRuleService service;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_ACCUMULATION_POINTS_RULE')")
    @Operation(summary = "Get all rules paged")
    public ResponseEntity<BaseResponse<Page<AccumulationPointsRuleResponse>>> getAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<AccumulationPointsRuleResponse> payload = service.getAll(pageable);

        BaseResponse<Page<AccumulationPointsRuleResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Accumulation Points Rules found"
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ACCUMULATION_POINTS_RULE')")
    @Operation(summary = "Create a new rule")
    public ResponseEntity<BaseResponse<Long>> create(@RequestBody NewAccumulationPointsRule request) {
        long payload = service.create(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Accumulation Points Rule created"
        );
    }

    @PutMapping
    @PreAuthorize("hasAuthority('UPDATE_ACCUMULATION_POINTS_RULE')")
    @Operation(summary = "Update an existing rule")
    public ResponseEntity<BaseResponse<Long>> update(@RequestBody UpdateAccumulationPointsRule request) {
        long payload = service.update(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Accumulation Points Rule updated"
        );
    }

    @PutMapping("/activate/{idAccumulationPointsRule}")
    @PreAuthorize("hasAuthority('UPDATE_ACCUMULATION_POINTS_RULE')")
    @Operation(summary = "Activate a specific rule")
    public ResponseEntity<BaseResponse<Void>> activate(@PathVariable long idAccumulationPointsRule) {
        service.activate(idAccumulationPointsRule);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.OK,
                "Accumulation Points Rule activated"
        );
    }
}
