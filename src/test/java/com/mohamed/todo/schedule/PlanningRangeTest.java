package com.mohamed.todo.schedule;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class PlanningRangeTest {

    // "Today" is fixed at Oct 30, 2026 for every test
    private static final ZoneId ZONE = ZoneId.of("UTC");
    private final Clock fixedClock = Clock.fixed(
            LocalDate.of(2026, 10, 30).atStartOfDay(ZONE).toInstant(), ZONE);

    private final PlanningRange range = new PlanningRange(fixedClock);

    @Test
    void firstAllowedDate_isJanFirst_fiveYearsBack() {
        assertThat(range.firstAllowedDate()).isEqualTo(LocalDate.of(2021, 1, 1));
    }

    @Test
    void lastAllowedDate_isDecThirtyFirst_fiveYearsAhead() {
        assertThat(range.lastAllowedDate()).isEqualTo(LocalDate.of(2031, 12, 31));
    }

    @Test
    void boundaryDays_areAllowed() {
        assertThat(range.contains(LocalDateTime.of(2021, 1, 1, 0, 0))).isTrue();
        assertThat(range.contains(LocalDateTime.of(2031, 12, 31, 23, 45))).isTrue();
    }

    @Test
    void daysJustOutside_areRejected() {
        assertThat(range.contains(LocalDateTime.of(2020, 12, 31, 23, 45))).isFalse();
        assertThat(range.contains(LocalDateTime.of(2032, 1, 1, 0, 0))).isFalse();
    }

    @Test
    void today_isAllowed() {
        assertThat(range.contains(LocalDateTime.of(2026, 10, 30, 14, 0))).isTrue();
    }
}