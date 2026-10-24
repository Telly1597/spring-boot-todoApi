package com.mohamed.todo;

import com.mohamed.todo.dto.TaskRequest;
import com.mohamed.todo.dto.TaskResponse;

public final class TaskMapper {

    private TaskMapper() {
        // utility class: static methods only, never instantiated
    }

    public static Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDone(request.done());
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.isDone());
    }
}
