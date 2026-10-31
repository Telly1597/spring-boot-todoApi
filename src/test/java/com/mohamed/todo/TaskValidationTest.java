package com.mohamed.todo;

import com.mohamed.todo.dto.TaskRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskValidationTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 2, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 11, 2, 15, 0);

    @Test
    void emptyTitle_isRejected() {
        TaskRequest request = new TaskRequest(" ", null, START, END, false);

        Set<ConstraintViolation<TaskRequest>> errors = validator.validate(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.iterator().next().getMessage())
                .isEqualTo("Title cannot be empty");
    }

    @Test
    void validTitle_isAccepted() {
        TaskRequest request = new TaskRequest("Buy milk", null, START, END, false);

        assertThat(validator.validate(request)).isEmpty();
    }
}
