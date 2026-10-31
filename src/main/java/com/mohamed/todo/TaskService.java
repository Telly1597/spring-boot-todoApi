package com.mohamed.todo;

import com.mohamed.todo.error.InvalidScheduleException;
import com.mohamed.todo.error.TaskNotFoundException;
import com.mohamed.todo.schedule.PlanningRange;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private static final int SLOT_MINUTES = 15;

    private final TaskRepository repo;
    private final PlanningRange planningRange;

    public TaskService(TaskRepository repo, PlanningRange planningRange) {
        this.repo = repo;
        this.planningRange = planningRange;
    }

    public List<Task> getAll() {
        return repo.findAll();
    }

    public Task getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        validateSchedule(task.getStartTime(), task.getEndTime());
        return repo.save(task);
    }

    public Task update(Long id, Task updated) {
        validateSchedule(updated.getStartTime(), updated.getEndTime());

        Task task = getById(id);
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setStartTime(updated.getStartTime());
        task.setEndTime(updated.getEndTime());
        task.setDone(updated.isDone());
        return repo.save(task);
    }

    public void delete(Long id) {
        getById(id); // throws TaskNotFoundException if missing
        repo.deleteById(id);
    }

    private void validateSchedule(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new InvalidScheduleException("startTime", "Start time is required");
        }
        if (end == null) {
            throw new InvalidScheduleException("endTime", "End time is required");
        }
        if (!end.isAfter(start)) {
            throw new InvalidScheduleException("endTime", "End time must be after start time");
        }
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new InvalidScheduleException("endTime", "Task must start and end on the same day");
        }
        if (!isOnSlotBoundary(start)) {
            throw new InvalidScheduleException("startTime", "Start time must be on a 15-minute mark (e.g. 14:00, 14:15)");
        }
        if (!isOnSlotBoundary(end)) {
            throw new InvalidScheduleException("endTime", "End time must be on a 15-minute mark (e.g. 14:00, 14:15)");
        }
        if (Duration.between(start, end).toMinutes() < SLOT_MINUTES) {
            throw new InvalidScheduleException("endTime", "Task must be at least 15 minutes long");
        }
        if (!planningRange.contains(start)) {
            throw new InvalidScheduleException("startTime",
                    "Date must be between " + planningRange.firstAllowedDate()
                            + " and " + planningRange.lastAllowedDate());
        }
    }

    private boolean isOnSlotBoundary(LocalDateTime time) {
        return time.getMinute() % SLOT_MINUTES == 0
                && time.getSecond() == 0
                && time.getNano() == 0;
    }
}
