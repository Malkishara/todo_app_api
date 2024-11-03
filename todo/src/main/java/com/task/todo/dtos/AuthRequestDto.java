package com.task.todo.dtos;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;
}
