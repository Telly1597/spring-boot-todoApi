package com.mohamed.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mohamed.todo.error.TaskConflictException; // rename to your exception

@SpringBootTest
@Transactional
class TaskConflictIntegrationTest {

    @Autowired TaskService service;
    @Autowired TaskRepository repo;

    Task existing; // 10:00-12:00

    @BeforeEach
    void setUp() {
        existing = service.create(task("existing", at(10, 0), at(12, 0)));
    }

    // ---------- Refused ----------

    @Test void create_refuses_sameSlot()           { assertCreateConflict(at(10, 0),  at(12, 0)); }
    @Test void create_refuses_overlapAtStart()     { assertCreateConflict(at(9, 0),   at(11, 0)); }
    @Test void create_refuses_overlapAtEnd()       { assertCreateConflict(at(11, 0),  at(13, 0)); }
    @Test void create_refuses_insideAnotherTask()  { assertCreateConflict(at(10, 30), at(11, 30)); }
    @Test void create_refuses_wrappingAnotherTask(){ assertCreateConflict(at(9, 0),   at(13, 0)); }

    @Test
    void update_refuses_movingIntoAnotherTasksSlot() {
        Task other = service.create(task("other", at(14, 0), at(16, 0)));

        assertThatThrownBy(() -> service.update(other.getId(), task("other", at(11, 0), at(13, 0))))
                .isInstanceOf(TaskConflictException.class);
    }

    // ---------- Allowed ----------

    @Test
    void create_allows_backToBackTasks() {
        service.create(task("before", at(8, 0), at(10, 0)));   // ends when existing starts
        service.create(task("after", at(12, 0), at(14, 0)));   // starts when existing ends

        assertThat(repo.count()).isEqualTo(3);
    }

    @Test
    void update_allows_movingTaskWithinItsOwnSlot() {
        // 10-12 -> 11-13 overlaps its own old slot, so this only passes if excludeId works
        Task updated = service.update(existing.getId(), task("existing", at(11, 0), at(13, 0)));

        assertThat(updated.getStartTime()).isEqualTo(at(11, 0));
        assertThat(updated.getEndTime()).isEqualTo(at(13, 0));
    }

    // ---------- helpers ----------

    private void assertCreateConflict(LocalDateTime start, LocalDateTime end) {
        assertThatThrownBy(() -> service.create(task("new", start, end)))
                .isInstanceOf(TaskConflictException.class);
        assertThat(repo.count()).isEqualTo(1); // nothing extra was saved
    }

    private static LocalDateTime at(int hour, int minute) {
        return LocalDateTime.of(2026, 11, 9, hour, minute); // inside your planning range, on a 15-min mark
    }

    private static Task task(String title, LocalDateTime start, LocalDateTime end) {
        Task t = new Task();
        t.setTitle(title);
        t.setStartTime(start);
        t.setEndTime(end);
        return t;
    }
}
