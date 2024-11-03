package com.task.todo.controllers;

import com.task.todo.dtos.AuthRequestDto;
import com.task.todo.dtos.AuthenticateUserDto;
import com.task.todo.services.UserService;
import com.task.todo.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationController(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDto authRequestDto) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(authRequestDto.getEmail());

            // Verify password
            if (!passwordEncoder.matches(authRequestDto.getPassword(), userDetails.getPassword())) {
                log.error("Invalid password for user: {}", authRequestDto.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            // Authenticate the user
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword()));

            AuthenticateUserDto authenticateUserDto = new AuthenticateUserDto();
            authenticateUserDto.setUser(userService.getUserByEmail(userDetails.getUsername()));
            authenticateUserDto.setJwtToken(jwtUtil.generateToken(userDetails.getUsername()));
            log.info("Login Successful");
            return new ResponseEntity<>(authenticateUserDto, HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", authRequestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (DisabledException e) {
            log.error("User disabled: {}", authRequestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is not activated");
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", authRequestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
