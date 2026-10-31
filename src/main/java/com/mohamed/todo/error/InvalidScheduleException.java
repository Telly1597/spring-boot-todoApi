package com.mohamed.todo.error;

public class InvalidScheduleException extends RuntimeException {
    private final String field;
    public InvalidScheduleException(String field, String message) {

        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
