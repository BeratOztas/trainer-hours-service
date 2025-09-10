package com.epam.gym.trainer_hours_service.security;

import com.epam.gym.trainer_hours_service.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtConfig jwtConfig;

    private static final String SECRET_KEY = "this-is-a-very-long-and-secure-secret-key-for-testing";
    private Key testKey;

    @BeforeEach
    void setUp() {
        // Set up the mock JwtConfig to return a known secret key
        when(jwtConfig.getSecret()).thenReturn(SECRET_KEY);

        // Manually set the key in the JwtTokenProvider instance using reflection.
        // This is necessary because getKey() is a private method.
        // In a real application, you might make it package-private for easier testing.
        testKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtConfig", jwtConfig);
    }

    private String generateTestToken(String username, long expirationMillis) {
        return Jwts.builder()
                .claim("username", username)
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(expirationMillis)))
                .signWith(testKey)
                .compact();
    }

    private String generateInvalidTestToken(String username, long expirationMillis, String invalidSecret) {
        Key invalidKey = Keys.hmacShaKeyFor(invalidSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("username", username)
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(expirationMillis)))
                .signWith(invalidKey)
                .compact();
    }

    @Test
    @DisplayName("should validate a token with a correct signature and expiration")
    void validateToken_withValidToken_shouldReturnTrue() {
        // Arrange
        String validToken = generateTestToken("testuser", 3600000); // 1 hour expiration

        // Act
        boolean isValid = jwtTokenProvider.validateToken(validToken);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("should return false for a token with an invalid signature")
    void validateToken_withInvalidSignature_shouldReturnFalse() {
        // Arrange
        String invalidToken = generateInvalidTestToken("testuser", 3600000, "another-secret-that-does-not-match-the-original");

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("should return false for an expired token")
    void validateToken_withExpiredToken_shouldReturnFalse() throws InterruptedException {
        // Arrange
        // Create a token that expires in 1 millisecond
        String expiredToken = generateTestToken("testuser", 1);
        
        // Wait for the token to expire
        Thread.sleep(10); 

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("should return false for a malformed token")
    void validateToken_withMalformedToken_shouldReturnFalse() {
        // Arrange
        String malformedToken = "invalid-token-format";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("should extract username from a valid token")
    void getUsernameFromToken_withValidToken_shouldReturnUsername() {
        // Arrange
        String username = "testuser";
        String validToken = generateTestToken(username, 3600000); // 1 hour expiration

        // Act
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(validToken);

        // Assert
        assertEquals(username, extractedUsername);
    }
}
