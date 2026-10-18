# To-Do List App

A simple to-do list built with Java and Spring Boot, my first Spring project.

## Features
- Add, complete, and delete tasks
- REST API with full CRUD
- Input validation and 404 error handling
- H2 database

## Tech Stack
Java 17, Spring Boot, Spring Data JPA, H2, HTML/JavaScript

## How to Run
1. Clone the repo
2. Open in IntelliJ and run `TodoApplication`
3. Visit http://localhost:8080

## API Endpoints
| Method | URL | Description |
|---|---|---|
| GET | /tasks | List all tasks |
| GET | /tasks/{id} | Get one task |
| POST | /tasks | Create a task |
| PUT | /tasks/{id} | Update a task |
| DELETE | /tasks/{id} | Delete a task |

## JDBC Setup

Start the application, then open the [H2 Console](http://localhost:8080/h2-console/).

Enter the following connection details:

| Setting | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:file:~/todo-app/planner` |
| Username | `sa` |
| Password | `telly224` |

Click **Connect** to access the database.