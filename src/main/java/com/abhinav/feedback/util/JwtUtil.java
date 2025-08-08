package com.abhinav.feedback.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // It's crucial to load this from application.properties or environment variables
    // For development, you can use a base64 encoded string of a strong secret.
    // Example: @Value("${jwt.secret:}")
    // For production, consider using a more robust key management strategy.
    // This example uses a dynamically generated key for simplicity, but in a real app,
    // you'd want a consistent key across restarts.
    private final SecretKey SECRET_KEY;

    // Constructor to initialize the secret key
    public JwtUtil(@Value("${jwt.secret:}") String secretBase64) {
        // If a secret is provided in application.properties, decode it.
        // Otherwise, generate a new secure key for HS256 algorithm.
        // In a real application, you would typically use a fixed, securely stored secret.
        if (secretBase64 != null && !secretBase64.isEmpty()) {
            this.SECRET_KEY = Keys.hmacShaKeyFor(secretBase64.getBytes());
        } else {
            // WARNING: Generating a new key on each startup is NOT suitable for production.
            // Tokens signed with a previous key will become invalid.
            // This is for demonstration purposes if no secret is configured.
            this.SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.out.println("WARNING: JWT Secret Key not configured in application.properties. " +
                    "A new key has been generated, which will invalidate previous tokens. " +
                    "Please set 'jwt.secret' for production.");
        }
    }

    /**
     * Generates a new JWT token for the given username.
     *
     * @param username The username for whom the token is generated.
     * @return The generated JWT token string.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                // Token valid for 10 hours (1000 ms * 60 s * 60 min * 10 hours)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Use the SecretKey object
                .compact();
    }

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token The JWT token string.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param token The JWT token string.
     * @return The expiration date extracted from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     *
     * @param token The JWT token string.
     * @param claimsResolver A function to resolve the desired claim from the Claims object.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token string.
     * @return The Claims object containing all claims from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // Use the SecretKey object
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates the given JWT token against the user details.
     * Checks if the username matches and if the token is not expired.
     *
     * @param token The JWT token string.
     * @param userDetails The UserDetails object containing the expected username.
     * @return True if the token is valid for the user, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the given JWT token has expired.
     *
     * @param token The JWT token string.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
