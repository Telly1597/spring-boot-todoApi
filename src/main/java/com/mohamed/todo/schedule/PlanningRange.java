package com.mohamed.todo.schedule;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class PlanningRange {

    private static final int YEARS_EACH_WAY = 5;

    private final Clock clock;

    public PlanningRange(Clock clock) {
        this.clock = clock;
    }

    public LocalDate firstAllowedDate() {
        int year = LocalDate.now(clock).getYear() - YEARS_EACH_WAY;
        return LocalDate.of(year, 1, 1);
    }

    public LocalDate lastAllowedDate() {
        int year = LocalDate.now(clock).getYear() + YEARS_EACH_WAY;
        return LocalDate.of(year, 12, 31);
    }

    public boolean contains(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        return !date.isBefore(firstAllowedDate()) && !date.isAfter(lastAllowedDate());
    }
}
