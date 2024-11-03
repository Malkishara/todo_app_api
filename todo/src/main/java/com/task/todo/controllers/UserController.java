package com.task.todo.controllers;

import com.task.todo.dtos.AuthenticateUserDto;
import com.task.todo.dtos.SignupRequestDto;
import com.task.todo.models.User;
import com.task.todo.services.UserService;
import com.task.todo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequestDto signupRequestDto) {
        log.info("Received signup request for email: {}", signupRequestDto.getEmail());

        try {
            // create the user
            User createdUser = userService.createUser(signupRequestDto);

            // Generate JWT token for the new user
            String token = jwtUtil.generateToken(createdUser.getEmail());

            // Prepare the response DTO with user info and token
            AuthenticateUserDto authenticateUserDto = new AuthenticateUserDto();
            authenticateUserDto.setUser(createdUser);
            authenticateUserDto.setJwtToken(token);

            log.info("User with email {} successfully registered", signupRequestDto.getEmail());
            return new ResponseEntity<>(authenticateUserDto, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Handle duplicate email exception
            log.error("Signup failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Handle any other unexpected exceptions
            log.error("Error occurred during signup for email {}: {}", signupRequestDto.getEmail(), e.getMessage(), e);
            return new ResponseEntity<>("An error occurred while processing your signup request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
