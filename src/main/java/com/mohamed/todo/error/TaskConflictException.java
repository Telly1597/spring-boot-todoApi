package com.mohamed.todo.error;

public class TaskConflictException extends RuntimeException {
    public TaskConflictException(String message) {
        super(message);
    }
}
