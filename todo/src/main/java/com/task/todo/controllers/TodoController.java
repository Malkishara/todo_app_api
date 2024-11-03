package com.task.todo.controllers;

import com.task.todo.dtos.CreateTodoRequestDto;
import com.task.todo.dtos.TodoDto;
import com.task.todo.models.Todo;
import com.task.todo.services.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PostMapping
    public ResponseEntity<?> createTodo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateTodoRequestDto createTodoRequestDto) {

        try {
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated. Please log in.");
            }

            Todo createdTodo = todoService.createTodo(createTodoRequestDto, userDetails.getUsername());
            return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authentication error: {}", e.getMessage());
            return new ResponseEntity<>("User is not logged in. Please log in.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error creating Todo: {}", e.getMessage());
            return new ResponseEntity<>("Todo not created, please try again later!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TodoDto todoDto) {

        try {
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated. Please log in.");
            }

            Todo updatedTodo = todoService.updateTodo(todoDto, userDetails.getUsername());
            return updatedTodo != null ?
                    new ResponseEntity<>(updatedTodo, HttpStatus.OK) :
                    new ResponseEntity<>("Todo not found or unauthorized.", HttpStatus.NOT_FOUND);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authentication error: {}", e.getMessage());
            return new ResponseEntity<>("User is not logged in. Please log in.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error updating Todo: {}", e.getMessage());
            return new ResponseEntity<>("Todo not updated, please try again later!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated. Please log in.");

            }

            Todo todo = todoService.getTodoById(id, userDetails.getUsername());
            return todo != null ?
                    new ResponseEntity<>(todo, HttpStatus.OK) :
                    new ResponseEntity<>("Todo not found.", HttpStatus.NOT_FOUND);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authentication error: {}", e.getMessage());
            return new ResponseEntity<>("User is not logged in. Please log in.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error retrieving todo: {}", e.getMessage());
            return new ResponseEntity<>("Failed to fetch todo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getTodosByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {

        try {
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated. Please log in.");
            }

            Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Todo> todosPage = todoService.getTodosByUser(userDetails.getUsername(), pageRequest, search);
            return new ResponseEntity<>(todosPage, HttpStatus.OK);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authentication error: {}", e.getMessage());
            return new ResponseEntity<>("User is not logged in. Please log in.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error retrieving todos: {}", e.getMessage());
            return new ResponseEntity<>("Failed to fetch todos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodoById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated. Please log in.");
            }

            boolean isDeleted = todoService.deleteTodoById(id, userDetails.getUsername());
            return isDeleted ?
                    new ResponseEntity<>("Todo deleted successfully.", HttpStatus.OK) :
                    new ResponseEntity<>("Todo not deleted, please try again later!", HttpStatus.BAD_REQUEST);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authentication error: {}", e.getMessage());
            return new ResponseEntity<>("User is not logged in. Please log in.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error deleting Todo: {}", e.getMessage());
            return new ResponseEntity<>("Todo not deleted, please try again later!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/completion-status")
    public ResponseEntity<?> updateTodoCompletionStatus(
            @PathVariable Integer id,
            @RequestParam boolean isCompleted,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated. Please log in.");
            }

            Todo updatedTodo = todoService.updateTodoCompletionStatus(id, isCompleted);
            return updatedTodo != null ?
                    new ResponseEntity<>(updatedTodo, HttpStatus.OK) :
                    new ResponseEntity<>("Todo not found or unauthorized.", HttpStatus.NOT_FOUND);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Authentication error: {}", e.getMessage());
            return new ResponseEntity<>("User is not logged in. Please log in.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error updating Todo completion status: {}", e.getMessage());
            return new ResponseEntity<>("Todo completion status not updated, please try again later!", HttpStatus.BAD_REQUEST);
        }
    }
}
