package com.abhinav.feedback.security;

import com.abhinav.feedback.services.CustomUserDetailsService;
import com.abhinav.feedback.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException; // Import specific JWT exceptions for better handling
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                // Attempt to extract username from JWT
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                // Token is expired
                System.err.println("JWT Token has expired: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("JWT Token has expired.");
                return; // Stop the filter chain
            } catch (SignatureException | MalformedJwtException e) {
                // Token is invalid or tampered
                System.err.println("Invalid JWT Token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("Invalid JWT Token.");
                return; // Stop the filter chain
            } catch (Exception e) {
                // Catch any other unexpected JWT parsing errors
                System.err.println("An unexpected error occurred during JWT parsing: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
                response.getWriter().write("An internal server error occurred.");
                return; // Stop the filter chain
            }
        }

        // If username is found and no authentication is currently set in the SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the token (checks username and expiration again, but expiration is already handled above)
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                // This case should ideally not be hit if ExpiredJwtException is handled,
                // but good for explicit username mismatch or other validation failures.
                System.err.println("JWT token validation failed for user: " + username + " (username mismatch or other issue).");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("JWT Token validation failed.");
                return; // Stop the filter chain
            }
        }

        // Continue the filter chain if authentication was successful or no token was present
        filterChain.doFilter(request, response);
    }
}

