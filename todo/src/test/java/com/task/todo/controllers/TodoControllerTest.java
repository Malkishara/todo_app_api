package com.task.todo.controllers;

import com.task.todo.dtos.CreateTodoRequestDto;
import com.task.todo.dtos.TodoDto;
import com.task.todo.models.Todo;
import com.task.todo.services.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = User.withUsername("testuser").password("password").roles("USER").build();
    }

    @Test
    void testCreateTodo_Success() {
        CreateTodoRequestDto requestDto = new CreateTodoRequestDto();
        Todo todo = new Todo();

        when(todoService.createTodo(any(CreateTodoRequestDto.class), anyString())).thenReturn(todo);

        ResponseEntity<?> response = todoController.createTodo(userDetails, requestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateTodo_Unauthenticated() {
        ResponseEntity<?> response = todoController.createTodo(null, new CreateTodoRequestDto());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateTodo_Success() {
        TodoDto todoDto = new TodoDto();
        Todo todo = new Todo();

        when(todoService.updateTodo(any(TodoDto.class), anyString())).thenReturn(todo);

        ResponseEntity<?> response = todoController.updateTodo(userDetails, todoDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTodoById_Success() {
        Todo todo = new Todo();

        when(todoService.getTodoById(anyInt(), anyString())).thenReturn(todo);

        ResponseEntity<?> response = todoController.getTodoById(1, userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTodosByUser_Success() {
        Page<Todo> todosPage = new PageImpl<>(Collections.singletonList(new Todo()));

        when(todoService.getTodosByUser(anyString(), any(PageRequest.class), anyString())).thenReturn(todosPage);

        ResponseEntity<?> response = todoController.getTodosByUser(userDetails, 0, 10, "dueDate", "asc", null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteTodoById_Success() {
        when(todoService.deleteTodoById(anyInt(), anyString())).thenReturn(true);

        ResponseEntity<?> response = todoController.deleteTodoById(1, userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
