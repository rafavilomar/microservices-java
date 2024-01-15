package com.microservice_level_up.error.invalid_expiration_date;

import java.time.YearMonth;

public class InvalidCardExpirationDateException extends RuntimeException {

    private final int month;
    private final int year;

    public InvalidCardExpirationDateException(int month, int year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public String getMessage() {
        YearMonth givenDate = YearMonth.of(year, month);
        YearMonth currentDate = YearMonth.now();

        return "The given expiration date (" +
                givenDate +
                ") must be after current date (" +
                currentDate +
                ")";
    }
}
