package com.epam.gym.trainer_hours_service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenExtractorTest {

    @InjectMocks
    private JwtTokenExtractor jwtTokenExtractor;

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("should extract a JWT token when a valid Bearer token is present")
    void extractJwtFromRequest_withValidBearerToken_shouldReturnToken() {
        // Arrange
        String token = "your.mock.jwt.token";
        String bearerToken = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);

        // Act
        String extractedToken = jwtTokenExtractor.extractJwtFromRequest(request);

        // Assert
        assertEquals(token, extractedToken);
    }

    @Test
    @DisplayName("should return null when the Authorization header is missing")
    void extractJwtFromRequest_withMissingHeader_shouldReturnNull() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        String extractedToken = jwtTokenExtractor.extractJwtFromRequest(request);

        // Assert
        assertNull(extractedToken);
    }

    @Test
    @DisplayName("should return null when the header does not have the Bearer prefix")
    void extractJwtFromRequest_withWrongPrefix_shouldReturnNull() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic your.mock.jwt.token");

        // Act
        String extractedToken = jwtTokenExtractor.extractJwtFromRequest(request);

        // Assert
        assertNull(extractedToken);
    }

    @Test
    @DisplayName("should return null when the Authorization header is blank")
    void extractJwtFromRequest_withBlankHeader_shouldReturnNull() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("   ");

        // Act
        String extractedToken = jwtTokenExtractor.extractJwtFromRequest(request);

        // Assert
        assertNull(extractedToken);
    }
}
