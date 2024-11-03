package com.task.todo.services;

import com.task.todo.dtos.SignupRequestDto;
import com.task.todo.models.User;
import com.task.todo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(email);

            if (user == null) {
                log.warn("User with email {} not found", email);
                throw new UsernameNotFoundException("User not found");
            }
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());

        } catch (Exception e) {
            log.error("Error while loading user by username: {}", email, e);
            throw new UsernameNotFoundException("An error occurred while fetching user details.");
        }
    }

    @Override
    public User createUser(SignupRequestDto signupRequestDto) {
        log.info("Attempting to create user with email: {}", signupRequestDto.getEmail());

        try {
            if (userRepository.findByEmail(signupRequestDto.getEmail()) != null) {
                log.warn("User with email {} already exists", signupRequestDto.getEmail());
                throw new IllegalArgumentException("Email is already taken.");
            }

            User user = new User();
            user.setName(signupRequestDto.getName());
            user.setEmail(signupRequestDto.getEmail());
            user.setPassword(new BCryptPasswordEncoder().encode(signupRequestDto.getPassword()));

            userRepository.save(user);



            log.info("Successfully created user with ID: {}", user.getId());
            return user;

        } catch (IllegalArgumentException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating user with email: {}", signupRequestDto.getEmail(), e);
            throw new RuntimeException("An error occurred while creating the user.");
        }
    }



    @Override
    public User getUserByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                log.warn("User with email {} not found", email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return user;

        } catch (UsernameNotFoundException e) {
            log.error("Error finding user by email: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching user by email: {}", email, e);
            throw new RuntimeException("An error occurred while fetching user by email.");
        }
    }
}
