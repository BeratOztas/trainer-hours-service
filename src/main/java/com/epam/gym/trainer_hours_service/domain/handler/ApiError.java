package com.epam.gym.trainer_hours_service.domain.handler;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ApiError {
	private final Integer status;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
    private final String hostName;
    
    public ApiError(Integer status, String message, String path, String hostName) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.hostName = hostName;
    }

}
