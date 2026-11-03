package com.mohamed.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskWeekQueryIntegrationTest {

    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository repo;

    // Real clock, so dates are relative to today and always inside the planning range
    private static final LocalDate MONDAY =
            LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    private static final LocalDate SUNDAY = MONDAY.plusDays(6);

    @BeforeEach
    void setUp() {
        repo.deleteAll();

        // Saved deliberately out of order, so the test proves the query sorts them
        save("Next week",            MONDAY.plusDays(9).atTime(10, 0),  MONDAY.plusDays(9).atTime(11, 0));
        save("Wednesday",            MONDAY.plusDays(2).atTime(9, 0),   MONDAY.plusDays(2).atTime(10, 0));
        save("Last week",            MONDAY.minusDays(4).atTime(10, 0), MONDAY.minusDays(4).atTime(11, 0));
        save("Sunday late",          SUNDAY.atTime(23, 0),              SUNDAY.atTime(23, 45));
        save("Monday morning",       MONDAY.atTime(8, 0),               MONDAY.atTime(9, 0));
        save("Next Monday midnight", MONDAY.plusDays(7).atTime(0, 0),   MONDAY.plusDays(7).atTime(0, 15));
        save("Previous Sunday late", MONDAY.minusDays(1).atTime(23, 0), MONDAY.minusDays(1).atTime(23, 45));
    }

    private void save(String title, LocalDateTime start, LocalDateTime end) {
        Task task = new Task();
        task.setTitle(title);
        task.setStartTime(start);
        task.setEndTime(end);
        service.create(task);
    }

    private List<String> titlesBetween(LocalDate from, LocalDate to) {
        return service.getBetween(from, to).stream().map(Task::getTitle).toList();
    }

    @Test
    void currentWeek_returnsOnlyItsTasks_sortedByStart() {
        assertThat(titlesBetween(MONDAY, SUNDAY))
                .containsExactly("Monday morning", "Wednesday", "Sunday late");
    }

    @Test
    void sundayLateTask_belongsToItsOwnWeek_notTheNext() {
        assertThat(titlesBetween(MONDAY, SUNDAY)).contains("Sunday late");
        assertThat(titlesBetween(MONDAY.plusDays(7), SUNDAY.plusDays(7))).doesNotContain("Sunday late");
    }

    @Test
    void nextMondayMidnight_isExcludedFromCurrentWeek() {
        assertThat(titlesBetween(MONDAY, SUNDAY)).doesNotContain("Next Monday midnight");
    }

    @Test
    void previousWeek_returnsOnlyItsTasks() {
        assertThat(titlesBetween(MONDAY.minusDays(7), SUNDAY.minusDays(7)))
                .containsExactly("Last week", "Previous Sunday late");
    }

    @Test
    void nextWeek_returnsOnlyItsTasks() {
        assertThat(titlesBetween(MONDAY.plusDays(7), SUNDAY.plusDays(7)))
                .containsExactly("Next Monday midnight", "Next week");
    }
}
