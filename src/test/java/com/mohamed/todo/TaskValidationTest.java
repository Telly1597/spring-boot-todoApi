package com.mohamed.todo;


import com.mohamed.todo.dto.TaskRequest;
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
        TaskRequest request = new TaskRequest(" ", false);


        Set<ConstraintViolation<TaskRequest>> errors = validator.validate(request);

        assertThat(errors).isNotEmpty();
        assertThat(errors.iterator().next().getMessage())
                .isEqualTo("Title cannot be empty");
    }

    @Test
    void validTitle_isAccepted() {
        TaskRequest request = new TaskRequest("Buy milk", false);

        assertThat(validator.validate(request)).isEmpty();
    }
}
