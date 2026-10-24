package com.mohamed.todo.dto;

import jakarta.validation.constraints.NotBlank;
public record TaskRequest(
        @NotBlank(message = "Title cannot be empty") String title,
        boolean done
) {}