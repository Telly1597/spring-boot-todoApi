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
        task.setDescription(request.description());
        task.setStartTime(request.startTime());
        task.setEndTime(request.endTime());
        task.setDone(request.done());

        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(),task.getDescription(), task.getStartTime(), task.getEndTime(), task.isDone());
    }
}
