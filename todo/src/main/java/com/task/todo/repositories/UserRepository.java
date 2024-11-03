package com.task.todo.repositories;

import com.task.todo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);

}
