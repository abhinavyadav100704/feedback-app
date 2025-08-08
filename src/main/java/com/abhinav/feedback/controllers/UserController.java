package com.abhinav.feedback.controllers;

import com.abhinav.feedback.dto.AuthRequest;
import com.abhinav.feedback.dto.AuthResponse;
import com.abhinav.feedback.dto.UserRequest;
import com.abhinav.feedback.dto.UserResponse;
import com.abhinav.feedback.entities.User;
import com.abhinav.feedback.services.UserService;
import com.abhinav.feedback.util.JwtUtil;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRequest userRequest) {
        // The UserService will handle password encoding and duplicate checks.
        // Any exceptions (e.g., RuntimeException for duplicate user) will be
        // caught by the GlobalExceptionHandler.
        User registeredUser = userService.registerUser(userRequest);
        // It's good practice not to return the password in the response
        registeredUser.setPassword(null); // Clear password before sending response
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // Authenticate the user using Spring Security's AuthenticationManager.
        // If authentication fails (e.g., bad credentials), an AuthenticationException
        // will be thrown and caught by the GlobalExceptionHandler.
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // Get the UserDetails from the authenticated principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate JWT token using the username from UserDetails
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // Return the token and username in a structured AuthResponse DTO
        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername()));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only users with ROLE_ADMIN can access this
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
