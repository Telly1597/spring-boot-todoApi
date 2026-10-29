package com.mohamed.todo;

import com.mohamed.todo.error.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;


import static org.assertj.core.api.Assertions.*;


@SpringBootTest
public class TaskIntegrationTest {
    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository repo;

    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();  // start each test with an empty table;
    }

    private Task newTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        return task;
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

        Task changes = newTask("After");
        changes.setDone(true);
        service.update(saved.getId(), changes);

        Task fromDb = service.getById(saved.getId());
        assertThat(fromDb.getTitle()).isEqualTo("After");
        assertThat(fromDb.isDone()).isTrue();
    }

    @Test
    void deleteTask_isGone() {
        Task saved = service.create(newTask("Delete me"));

        service.delete(saved.getId());

        assertThatThrownBy(() -> service.getById(saved.getId()))
                .isInstanceOf(TaskNotFoundException.class);
    }


}
