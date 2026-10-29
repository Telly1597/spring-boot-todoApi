package com.mohamed.todo.error;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
    // Compact constructor: fieldErrors is never null and can't be changed afterwards
    public ErrorResponse {
        fieldErrors = (fieldErrors == null) ? Map.of() : Map.copyOf(fieldErrors);
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return of(status, message, Map.of());
    }

    public static ErrorResponse of(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(status.value(), status.getReasonPhrase(), message, fieldErrors);
    }
}
