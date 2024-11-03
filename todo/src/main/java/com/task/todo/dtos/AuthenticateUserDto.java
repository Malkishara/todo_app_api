package com.task.todo.dtos;

import com.task.todo.models.User;
import lombok.Data;

@Data
public class AuthenticateUserDto {
    private User user;
    private String jwtToken;

}
