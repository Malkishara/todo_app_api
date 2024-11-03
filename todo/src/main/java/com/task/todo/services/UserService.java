package com.task.todo.services;

import com.task.todo.dtos.SignupRequestDto;
import com.task.todo.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
     UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

     User createUser(SignupRequestDto signupRequestDto);

    User getUserByEmail(String username);
}
