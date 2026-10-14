package com.mohamed.todo;

import org.springframework.web.bind.annotation.*;

@RestController
public class TodoController {
    @GetMapping("/welcome")
    public String Welcome() {
        return "Hello, Welcome to a simple todo list application";
    }

}
