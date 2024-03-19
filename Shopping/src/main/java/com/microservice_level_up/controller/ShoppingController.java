package com.microservice_level_up.controller;

import com.microservice_level_up.dto.InvoiceResponse;
import com.microservice_level_up.dto.PurchaseRequest;
import com.microservice_level_up.response.BaseResponse;
import com.microservice_level_up.service.IPurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shopping")
public record ShoppingController (IPurchaseService purchaseService) {

    @PostMapping("/purchase")
    public ResponseEntity<BaseResponse<InvoiceResponse>> purchase(@RequestBody PurchaseRequest request) {
        BaseResponse<InvoiceResponse> response = new BaseResponse<>();
        return response.buildResponseEntity(
                purchaseService.purchase(request),
                HttpStatus.OK,
                "Purchase successfully"
        );
    }

}
