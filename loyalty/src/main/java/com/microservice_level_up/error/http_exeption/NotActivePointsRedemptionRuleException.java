package com.microservice_level_up.error.http_exeption;

public class NotActivePointsRedemptionRuleException extends BadRequestException {
    public NotActivePointsRedemptionRuleException(String message) {
        super(message);
    }

}
