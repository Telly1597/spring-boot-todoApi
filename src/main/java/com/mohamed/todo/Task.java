package com.mohamed.todo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message ="Title cannot be empty")
    private String title;

    private boolean done;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
