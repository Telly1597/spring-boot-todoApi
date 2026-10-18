package com.mohamed.todo;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskValidationTest {
    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void emptyTitle_isRejected() {
        Task task = new Task();
        task.setTitle(" ");

        Set<ConstraintViolation<Task>> errors = validator.validate(task);

        assertThat(errors).isNotEmpty();
        assertThat(errors.iterator().next().getMessage())
                .isEqualTo("Title cannot be empty");
    }

    @Test
    void validTitle_isAccepted() {
        Task task = new Task();
        task.setTitle("Buy milk");

        assertThat(validator.validate(task)).isEmpty();
    }
}
