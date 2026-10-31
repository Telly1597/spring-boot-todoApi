package com.mohamed.todo;

import com.mohamed.todo.error.InvalidScheduleException;
import com.mohamed.todo.error.TaskNotFoundException;
import com.mohamed.todo.schedule.PlanningRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository repo;  // fake repository, no real database

    private TaskService service;

    // "Today" is fixed at Oct 30, 2026, so the planning range is Jan 1, 2021 – Dec 31, 2031
    private static final ZoneId ZONE = ZoneId.of("UTC");
    private final PlanningRange planningRange = new PlanningRange(
            Clock.fixed(LocalDate.of(2026, 10, 30).atStartOfDay(ZONE).toInstant(), ZONE));

    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 2, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 11, 2, 15, 0);

    @BeforeEach
    void setUp() {
        service = new TaskService(repo, planningRange);
    }

    private Task newTask(String title, boolean done) {
        return newTask(title, done, START, END);
    }

    private Task newTask(String title, boolean done, LocalDateTime start, LocalDateTime end) {
        Task task = new Task();
        task.setTitle(title);
        task.setDone(done);
        task.setStartTime(start);
        task.setEndTime(end);
        return task;
    }

    /** Asserts that creating a task with these times is rejected on the given field. */
    private void assertCreateRejected(LocalDateTime start, LocalDateTime end,
                                      String expectedField, String expectedMessagePart) {
        Task task = newTask("Bad slot", false, start, end);

        assertThatThrownBy(() -> service.create(task))
                .isInstanceOfSatisfying(InvalidScheduleException.class, ex -> {
                    assertThat(ex.getField()).isEqualTo(expectedField);
                    assertThat(ex.getMessage()).contains(expectedMessagePart);
                });

        verify(repo, never()).save(any());
    }

    // ---------- Existing behaviour ----------

    @Test
    void getAll_returnsAllTasks() {
        when(repo.findAll()).thenReturn(List.of(newTask("A", false), newTask("B", true)));

        List<Task> result = service.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void getById_returnsTask_whenItExists() {
        when(repo.findById(1L)).thenReturn(Optional.of(newTask("Learn Spring", false)));

        Task result = service.getById(1L);

        assertThat(result.getTitle()).isEqualTo("Learn Spring");
    }

    @Test
    void getById_throwsNotFound_whenTaskMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task 99 not found");
    }

    @Test
    void create_savesTask() {
        Task task = newTask("New task", false);
        when(repo.save(task)).thenReturn(task);

        Task result = service.create(task);

        assertThat(result.getTitle()).isEqualTo("New task");
        verify(repo).save(task);
    }

    @Test
    void update_changesAllEditableFields() {
        Task existing = newTask("Old title", false);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        Task changes = newTask("New title", true, START.plusHours(2), END.plusHours(2));
        changes.setDescription("Moved later");

        Task result = service.update(1L, changes);

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.isDone()).isTrue();
        assertThat(result.getStartTime()).isEqualTo(START.plusHours(2));
        assertThat(result.getEndTime()).isEqualTo(END.plusHours(2));
        assertThat(result.getDescription()).isEqualTo("Moved later");
    }

    @Test
    void delete_throws404_andDoesNotDelete_whenTaskMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repo, never()).deleteById(any());
    }

    // ---------- Schedule rules: rejected ----------

    @Test
    void create_rejects_missingStartTime() {
        assertCreateRejected(null, END, "startTime", "required");
    }

    @Test
    void create_rejects_missingEndTime() {
        assertCreateRejected(START, null, "endTime", "required");
    }

    @Test
    void create_rejects_endBeforeStart() {
        assertCreateRejected(START, START.minusHours(1), "endTime", "after start");
    }

    @Test
    void create_rejects_endEqualToStart() {
        assertCreateRejected(START, START, "endTime", "after start");
    }

    @Test
    void create_rejects_taskSpanningTwoDays() {
        LocalDateTime lateEvening = LocalDateTime.of(2026, 11, 2, 23, 0);
        LocalDateTime nextMorning = LocalDateTime.of(2026, 11, 3, 1, 0);

        assertCreateRejected(lateEvening, nextMorning, "endTime", "same day");
    }

    @Test
    void create_rejects_startNotOnFifteenMinuteMark() {
        assertCreateRejected(START.withMinute(10), END, "startTime", "15-minute");
    }

    @Test
    void create_rejects_endNotOnFifteenMinuteMark() {
        assertCreateRejected(START, END.withMinute(50), "endTime", "15-minute");
    }

    @Test
    void create_rejects_startWithSeconds() {
        assertCreateRejected(START.withSecond(30), END, "startTime", "15-minute");
    }

    @Test
    void create_rejects_dateBeforePlanningRange() {
        LocalDateTime start = LocalDateTime.of(2020, 12, 31, 14, 0);

        assertCreateRejected(start, start.plusHours(1), "startTime", "between 2021-01-01 and 2031-12-31");
    }

    @Test
    void create_rejects_dateAfterPlanningRange() {
        LocalDateTime start = LocalDateTime.of(2032, 1, 1, 14, 0);

        assertCreateRejected(start, start.plusHours(1), "startTime", "between 2021-01-01 and 2031-12-31");
    }

    @Test
    void update_rejectsInvalidSlot_withoutLoadingOrSaving() {
        Task changes = newTask("Backwards", false, START, START.minusHours(1));

        assertThatThrownBy(() -> service.update(1L, changes))
                .isInstanceOf(InvalidScheduleException.class);

        verify(repo, never()).findById(any());
        verify(repo, never()).save(any());
    }

    // ---------- Schedule rules: allowed edge cases ----------

    @Test
    void create_allows_shortestTask_ofFifteenMinutes() {
        Task task = newTask("Quick call", false, START, START.plusMinutes(15));
        when(repo.save(task)).thenReturn(task);

        assertThatCode(() -> service.create(task)).doesNotThrowAnyException();
    }

    @Test
    void create_allows_lastSlotOfTheDay() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 2, 23, 30);
        Task task = newTask("Late task", false, start, start.plusMinutes(15)); // ends 23:45
        when(repo.save(task)).thenReturn(task);

        assertThatCode(() -> service.create(task)).doesNotThrowAnyException();
    }

    @Test
    void create_allows_firstAndLastDayOfPlanningRange() {
        Task first = newTask("First day", false,
                LocalDateTime.of(2021, 1, 1, 9, 0), LocalDateTime.of(2021, 1, 1, 10, 0));
        Task last = newTask("Last day", false,
                LocalDateTime.of(2031, 12, 31, 9, 0), LocalDateTime.of(2031, 12, 31, 10, 0));
        when(repo.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        assertThatCode(() -> service.create(first)).doesNotThrowAnyException();
        assertThatCode(() -> service.create(last)).doesNotThrowAnyException();
    }
}
