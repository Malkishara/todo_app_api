package com.task.todo.services;

import com.task.todo.dtos.CreateTodoRequestDto;
import com.task.todo.dtos.TodoDto;
import com.task.todo.models.Todo;
import com.task.todo.models.User;
import com.task.todo.repositories.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserService userService;

    @Override
    public Todo createTodo(CreateTodoRequestDto createTodoRequestDto, String username) {
        try {
            User user = userService.getUserByEmail(username);
            if (user == null) {
                log.error("User not found for username: {}", username);
                throw new IllegalArgumentException("User not found.");
            }

            Todo todo = Todo.builder()
                    .title(createTodoRequestDto.getTitle())
                    .description(createTodoRequestDto.getDescription())
                    .dueDate(createTodoRequestDto.getDueDate())
                    .priority(createTodoRequestDto.getPriority())
                    .isCompleted(false)
                    .user(user)
                    .build();

            return todoRepository.save(todo);
        } catch (Exception e) {
            log.error("Error creating Todo: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Todo.", e);
        }
    }

    @Override
    public Todo updateTodo(TodoDto todoDto, String username) {
        try {
            Optional<Todo> existingTodo = todoRepository.findById(todoDto.getId());
            if (existingTodo.isEmpty() || !existingTodo.get().getUser().getEmail().equals(username)) {
                log.warn("Todo not found or unauthorized access for username: {} and todo id: {}", username, todoDto.getId());
                return null;
            }

            Todo todo = existingTodo.get();
            todo.setTitle(todoDto.getTitle());
            todo.setDescription(todoDto.getDescription());
            todo.setDueDate(todoDto.getDueDate());
            todo.setPriority(todoDto.getPriority());
            todo.setCompleted(todoDto.isCompleted());

            return todoRepository.save(todo);
        } catch (Exception e) {
            log.error("Error updating Todo with ID {}: {}", todoDto.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update Todo.", e);
        }
    }

    @Override
    public Todo getTodoById(Integer id, String username) {
        try {
            Optional<Todo> todo = todoRepository.findById(id);
            return todo.filter(value -> value.getUser().getEmail().equals(username))
                    .orElseThrow(() -> new IllegalArgumentException("Todo not found or unauthorized."));
        } catch (Exception e) {
            log.error("Error retrieving Todo with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve Todo.", e);
        }
    }

    @Override
    public Page<Todo> getTodosByUser(String username, PageRequest pageRequest, String search) {
        try {
            User user = userService.getUserByEmail(username);
            if (user == null) {
                log.error("User not found for username: {}", username);
                throw new IllegalArgumentException("User not found.");
            }
            return search == null || search.isEmpty() ?
                    todoRepository.findByUser(user, pageRequest) :
                    todoRepository.findByUserAndTitleContainingIgnoreCase(user, search, pageRequest);
        } catch (Exception e) {
            log.error("Error retrieving Todos for user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve Todos.", e);
        }
    }

    @Override
    public boolean deleteTodoById(Integer id, String username) {
        try {
            Optional<Todo> todo = todoRepository.findById(id);
            if (todo.isPresent() && todo.get().getUser().getEmail().equals(username)) {
                todoRepository.deleteById(id);
                log.info("Todo with ID {} deleted successfully by user {}", id, username);
                return true;
            }
            log.warn("Todo with ID {} not found or unauthorized access for user {}", id, username);
            return false;
        } catch (Exception e) {
            log.error("Error deleting Todo with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete Todo.", e);
        }
    }

    @Override
    public Todo updateTodoCompletionStatus(Integer id, boolean isCompleted) {
        try {
            Optional<Todo> todoOptional = todoRepository.findById(id);
            if (todoOptional.isPresent()) {
                Todo todo = todoOptional.get();
                todo.setCompleted(isCompleted);
                log.info("Todo with ID {} completion status updated to {}", id, isCompleted);
                return todoRepository.save(todo);
            }
            log.warn("Todo with ID {} not found for completion status update.", id);
            return null;
        } catch (Exception e) {
            log.error("Error updating completion status for Todo with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update completion status.", e);
        }
    }
}
