package com.mohamed.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequest(
        @NotBlank(message = "Title cannot be empty") String title,
        @Size(max = 500, message = "Description can be at most 500 characters") String description,
        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime,

        boolean done
) {}