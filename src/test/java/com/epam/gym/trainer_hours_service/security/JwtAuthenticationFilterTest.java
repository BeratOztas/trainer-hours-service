package com.epam.gym.trainer_hours_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private JwtTokenExtractor tokenExtractor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // A valid token scenario
    @Test
    @DisplayName("should set authentication for a valid JWT token")
    void doFilterInternal_withValidJwt_shouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String validJwt = "valid.jwt.token";
        String username = "testuser";
        when(tokenExtractor.extractJwtFromRequest(request)).thenReturn(validJwt);
        when(tokenProvider.validateToken(validJwt)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(validJwt)).thenReturn(username);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenExtractor).extractJwtFromRequest(request);
        verify(tokenProvider).validateToken(validJwt);
        verify(tokenProvider).getUsernameFromToken(validJwt);
        
        // Assert that the SecurityContextHolder now holds a non-null authentication object.
        // It's not necessary to check the full details, just that it was set.
        assert (SecurityContextHolder.getContext().getAuthentication() != null);
    }
    
    // An invalid token scenario
    @Test
    @DisplayName("should not set authentication for an invalid JWT token")
    void doFilterInternal_withInvalidJwt_shouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String invalidJwt = "invalid.jwt.token";
        when(tokenExtractor.extractJwtFromRequest(request)).thenReturn(invalidJwt);
        when(tokenProvider.validateToken(invalidJwt)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenExtractor).extractJwtFromRequest(request);
        verify(tokenProvider).validateToken(invalidJwt);
        verify(tokenProvider, never()).getUsernameFromToken(anyString()); // Ensure username method is never called
        
        // Assert that no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // No token scenario
    @Test
    @DisplayName("should not set authentication when no JWT token is present")
    void doFilterInternal_withNoJwt_shouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(tokenExtractor.extractJwtFromRequest(request)).thenReturn(null);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenExtractor).extractJwtFromRequest(request);
        // Ensure no validation methods were called since no token was found
        verify(tokenProvider, never()).validateToken(anyString()); 
        verify(tokenProvider, never()).getUsernameFromToken(anyString());
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}