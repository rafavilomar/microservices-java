package com.microservice_level_up.module.point;

import com.microservice_level_up.module.point.dto.PurchaseRequest;
import com.microservice_level_up.module.point.dto.SimpleLotPoints;
import com.microservice_level_up.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/points")
public record PointsController(IPointsService service) {

    @GetMapping("{idCustomer}")
    public ResponseEntity<BaseResponse<Optional<SimpleLotPoints>>> getPointsInfo(@PathVariable("idCustomer") long idCustomer) {

        Optional<SimpleLotPoints> payload = service.getPointsInfo(idCustomer);

        BaseResponse<Optional<SimpleLotPoints>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Customer's points info"
        );
    }

    @PostMapping("/accumulatePoints")
    public ResponseEntity<BaseResponse<Void>> accumulatePoints(@RequestBody PurchaseRequest request) {
        service.accumulatePoints(request);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.CREATED,
                "Points accumulated"
        );
    }

    @PostMapping("/redeemPoints")
    public ResponseEntity<BaseResponse<Void>> redeemPoints(@RequestBody PurchaseRequest request) {
        service.redeemPoints(request);

        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.CREATED,
                "Points redeemed"
        );
    }
}