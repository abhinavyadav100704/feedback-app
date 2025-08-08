package com.abhinav.feedback.services;

import com.abhinav.feedback.dto.UserRequest;
import com.abhinav.feedback.dto.UserResponse;
import com.abhinav.feedback.entities.User;
import com.abhinav.feedback.exception.DuplicateUserException; // Import the new exception
import com.abhinav.feedback.exception.UserNotFoundException;
import com.abhinav.feedback.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRequest userRequest) {
        // 1. Check for duplicate username
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new DuplicateUserException("User with username '" + userRequest.getUsername() + "' already exists.");
        }

        // 2. Check for duplicate email (assuming email should also be unique)
        if (userRequest.getEmail() != null && userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new DuplicateUserException("User with email '" + userRequest.getEmail() + "' already exists.");
        }

        // 3. Create a new User entity from the UserRequest
        User user = new User(
                userRequest.getUsername(),
                userRequest.getEmail(),
                passwordEncoder.encode(userRequest.getPassword()), // Encode password
                userRequest.getRole() // Use the role from the UserRequest
        );

        // 4. Save the user
        logger.info("Registering new user: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Registers a new user.
     * Encodes the password and checks for duplicate usernames/emails before saving.
     * Throws DuplicateUserException if a user with the same username or email already exists.
     *
     * @param user The User object to register.
     * @return The saved User object.
     * @throws DuplicateUserException if a user with the same username or email already exists.
     */


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean checkUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setUsername(user.getUsername());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setRole(user.getRole());
                    userResponse.setCreatedAt(user.getCreatedAt());
                    return userResponse;
                })
                .collect(Collectors.toList());
    }
}
