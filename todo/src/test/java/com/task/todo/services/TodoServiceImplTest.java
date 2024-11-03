package com.task.todo.services;

import com.task.todo.dtos.CreateTodoRequestDto;
import com.task.todo.dtos.TodoDto;
import com.task.todo.models.Todo;
import com.task.todo.models.User;
import com.task.todo.repositories.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTodo_Success() {
        CreateTodoRequestDto requestDto = new CreateTodoRequestDto();
        User user = new User();
        Todo todo = new Todo();

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo createdTodo = todoService.createTodo(requestDto, "testuser");
        assertNotNull(createdTodo);
    }

    @Test
    void testUpdateTodo_Success() {
        TodoDto todoDto = new TodoDto();
        todoDto.setId(1);
        Todo todo = new Todo();
        todo.setUser(new User());

        when(todoRepository.findById(anyInt())).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo updatedTodo = todoService.updateTodo(todoDto, "testuser");
        assertNotNull(updatedTodo);
    }

    @Test
    void testGetTodoById_Success() {
        Todo todo = new Todo();
        todo.setUser(new User());

        when(todoRepository.findById(anyInt())).thenReturn(Optional.of(todo));

        Todo foundTodo = todoService.getTodoById(1, "testuser");
        assertNotNull(foundTodo);
    }

    @Test
    void testGetTodosByUser_Success() {
        User user = new User();
        Page<Todo> todosPage = new PageImpl<>(Collections.singletonList(new Todo()));

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(todoRepository.findByUser(any(User.class), any(PageRequest.class))).thenReturn(todosPage);

        Page<Todo> result = todoService.getTodosByUser("testuser", PageRequest.of(0, 10), null);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testDeleteTodoById_Success() {
        Todo todo = new Todo();
        todo.setUser(new User());

        when(todoRepository.findById(anyInt())).thenReturn(Optional.of(todo));

        boolean isDeleted = todoService.deleteTodoById(1, "testuser");
        assertEquals(true, isDeleted);
    }
}
