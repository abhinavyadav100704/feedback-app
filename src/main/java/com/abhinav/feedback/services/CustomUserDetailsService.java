package com.abhinav.feedback.services;

import com.abhinav.feedback.entities.User;
import com.abhinav.feedback.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user from the database using the UserRepository.
        // If the user is not found, throw a UsernameNotFoundException.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Create and return a Spring Security UserDetails object.
        // IMPORTANT: Ensure the role is prefixed with "ROLE_" if your database stores it without.
        // For example, if user.getRole() returns "ADMIN", it should become "ROLE_ADMIN".
        String roleWithPrefix = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),                  // The username
                user.getPassword(),                  // The encoded password
                Collections.singleton(new SimpleGrantedAuthority(roleWithPrefix)) // User's role(s) as authorities
        );
    }
}
