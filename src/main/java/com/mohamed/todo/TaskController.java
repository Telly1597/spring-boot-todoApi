package com.mohamed.todo;

import com.mohamed.todo.dto.TaskRequest;
import com.mohamed.todo.dto.TaskResponse;
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
    public List<TaskResponse> getAll() {
        return service.getAll().stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getOne(@PathVariable Long id) {

        return TaskMapper.toResponse(service.getById(id));
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        Task saved = service.create(TaskMapper.toEntity(request));
        return TaskMapper.toResponse(saved);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
       Task updated = service.update(id, TaskMapper.toEntity(request));
       return TaskMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {service.delete(id);}
}
