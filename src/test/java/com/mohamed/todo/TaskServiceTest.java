package com.mohamed.todo;

import com.mohamed.todo.error.TaskNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository repo;  // fake repository, no real database

    @InjectMocks
    private TaskService service; // real service, given the fake repo

    private Task newTask(String title, boolean done) {
        Task task = new Task();
        task.setTitle(title);
        task.setDone(done);

        return task;
    }

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
        verify(repo).save(task);  // check that save() was called
    }

    @Test
    void update_changesTitleAndDone() {
        Task existing = newTask("Old title", false);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        Task result = service.update(1L, newTask("New title", true));
        assertThat(result.isDone()).isTrue();
    }

    @Test
    void delete_throws404_andDoesNotDelete_whenTaskMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repo, never()).deleteById(any());
    }

}
