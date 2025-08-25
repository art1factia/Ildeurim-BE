package com.example.Ildeurim.exception.career;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate, String message) {
        super(message);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}