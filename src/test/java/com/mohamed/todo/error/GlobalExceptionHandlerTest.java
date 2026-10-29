package com.mohamed.todo.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void taskNotFound_returns404() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new TaskNotFoundException(42L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .isEqualTo(new ErrorResponse(404, "Not Found", "Task 42 not found", Map.of()));
    }

    @Test
    void scheduleConflict_returns409() {
        ResponseEntity<ErrorResponse> response =
                handler.handleConflict(new ScheduleConflictException("Overlaps with 'Gym' (14:00–15:00)"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody())
                .isEqualTo(new ErrorResponse(409, "Conflict", "Overlaps with 'Gym' (14:00–15:00)", Map.of()));
    }

    @Test
    void businessRule_returns409() {
        ResponseEntity<ErrorResponse> response =
                handler.handleConflict(new BusinessRuleException("Finish 2 remaining subtasks first"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().message()).isEqualTo("Finish 2 remaining subtasks first");
    }

    @Test
    void unexpectedError_returns500_withoutLeakingDetails() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(new IllegalStateException("DB password is hunter2"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message())
                .isEqualTo("Something went wrong. Please try again.")
                .doesNotContain("hunter2");
    }

    @Test
    void springClientError_keepsItsOwnStatus_insteadOf500() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(new HttpRequestMethodNotSupportedException("PATCH"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody().error()).isEqualTo("Method Not Allowed");
    }

    @Test
    void errorResponse_neverHasNullFieldErrors() {
        ErrorResponse response = new ErrorResponse(400, "Bad Request", "x", null);

        assertThat(response.fieldErrors()).isEmpty();
    }
}
