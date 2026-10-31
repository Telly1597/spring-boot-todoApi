package com.mohamed.todo;

import com.mohamed.todo.error.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TaskIntegrationTest {

    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository repo;

    private static final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();  // start each test with an empty table
    }

    private Task newTask(String title, LocalDateTime start, LocalDateTime end) {
        Task task = new Task();
        task.setTitle(title);
        task.setStartTime(start);
        task.setEndTime(end);
        return task;
    }

    private Task newTask(String title) {
        return newTask(title, TODAY.atTime(14, 0), TODAY.atTime(15, 0));
    }

    @Test
    void createdTask_isSavedWithAnId() {
        Task saved = service.create(newTask("Integration test"));

        assertThat(saved.getId()).isNotNull();
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void update_isSavedToDataBase() {
        Task saved = service.create(newTask("Before"));

        Task changes = newTask("After", TODAY.atTime(16, 0), TODAY.atTime(17, 30));
        changes.setDescription("Moved to the afternoon");
        changes.setDone(true);
        service.update(saved.getId(), changes);

        Task fromDb = service.getById(saved.getId());
        assertThat(fromDb.getTitle()).isEqualTo("After");
        assertThat(fromDb.isDone()).isTrue();
        assertThat(fromDb.getStartTime()).isEqualTo(TODAY.atTime(16, 0));
        assertThat(fromDb.getEndTime()).isEqualTo(TODAY.atTime(17, 30));
        assertThat(fromDb.getDescription()).isEqualTo("Moved to the afternoon");
    }

    @Test
    void deleteTask_isGone() {
        Task saved = service.create(newTask("Delete me"));

        service.delete(saved.getId());

        assertThatThrownBy(() -> service.getById(saved.getId()))
                .isInstanceOf(TaskNotFoundException.class);
    }
}
