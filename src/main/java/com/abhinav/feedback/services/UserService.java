package com.abhinav.feedback.services;

import com.abhinav.feedback.dto.UserRequest; // Import UserRequest
import com.abhinav.feedback.dto.UserResponse;
import com.abhinav.feedback.entities.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRequest userRequest); // Changed parameter to UserRequest

    User findByUsername(String username);

    User findByEmail(String email);

    boolean checkUserExists(String username);

    List<UserResponse> findAllUsers();
}