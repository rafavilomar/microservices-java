package com.microservice_level_up.error.invalid_expiration_date;

import java.time.YearMonth;

public class InvalidCardExpirationDateException extends RuntimeException {

    private final int month;
    private final int year;

    public InvalidCardExpirationDateException(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public String getMessage() {
        YearMonth givenDate = YearMonth.of(year, month);
        YearMonth currentDate = YearMonth.now();

        StringBuilder message = new StringBuilder().append("The given expiration date (");
        message.append(givenDate);
        message.append(") must be after current date (");
        message.append(currentDate);
        message.append(")");

        return message.toString();
    }
}
