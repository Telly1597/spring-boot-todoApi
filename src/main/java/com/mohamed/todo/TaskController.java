package com.mohamed.todo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Task getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Task create(@Valid @RequestBody Task task) {
        return service.create(task);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody Task task) {
        return service.update(id, task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {service.delete(id);}
}
