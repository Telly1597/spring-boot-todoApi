package com.mohamed.todo;
import com.mohamed.todo.error.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> getAll() {
        return repo.findAll();
    }

    public Task getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        return repo.save(task);
    }

    public Task update(Long id, Task updated) {
        Task task = getById(id);
        task.setTitle(updated.getTitle());
        task.setDone(updated.isDone());
        return repo.save(task);
    }

    public void delete(Long id) {
        getById(id); // throws a 404 if the task doesn't exist
        repo.deleteById(id);
    }

}
