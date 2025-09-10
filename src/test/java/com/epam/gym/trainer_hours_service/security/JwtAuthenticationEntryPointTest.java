package com.epam.gym.trainer_hours_service.security;

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationEntryPointTest {

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Test
    @DisplayName("should send 401 Unauthorized error with the exception message")
    void commence_shouldSendUnauthorizedError() throws IOException, ServletException {
        // Arrange
        String errorMessage = "Unauthorized: Full authentication is required to access this resource";
        
        // Use Mockito.when() to define the mock's behavior.
        // Tell the mock what to return when getMessage() is called.
        when(authException.getMessage()).thenReturn(errorMessage);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        // Now, you can verify that the response.sendError method
        // was called with the correct status and the message from the mock.
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }
}