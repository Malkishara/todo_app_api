package com.task.todo.repositories;

import com.task.todo.models.Todo;
import com.task.todo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TodoRepository extends JpaRepository<Todo,Integer> {
    Page<Todo> findByUser(User user, Pageable pageable);
    Page<Todo> findByUserAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);
}
