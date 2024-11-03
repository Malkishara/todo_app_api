package com.task.todo.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTodoRequestDto {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer priority;
    private boolean isCompleted;
}
