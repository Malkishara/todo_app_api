package com.task.todo.services;

import com.task.todo.dtos.CreateTodoRequestDto;
import com.task.todo.dtos.TodoDto;
import com.task.todo.models.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface TodoService {
    Todo createTodo(CreateTodoRequestDto createTodoRequestDto, String username);

    Todo updateTodo( TodoDto todoDto, String username);

    Todo getTodoById(Integer id, String username);

    Page<Todo> getTodosByUser(String username, PageRequest pageRequest, String search);

    boolean deleteTodoById(Integer id, String username);

    Todo updateTodoCompletionStatus(Integer id,  boolean isCompleted);
}
