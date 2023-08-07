package com.microservice_level_up.module.accumulation_points_rule;

import com.microservice_level_up.module.accumulation_points_rule.dto.AccumulationPointsRuleResponse;
import com.microservice_level_up.module.accumulation_points_rule.dto.NewAccumulationPointsRule;
import com.microservice_level_up.module.accumulation_points_rule.dto.UpdateAccumulationPointsRule;
import com.microservice_level_up.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accumulationPointsRule")
public record AccumulationPointsRuleController(IAccumulationPointsRuleService service) {

    @GetMapping
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
