package com.epam.gym.trainer_hours_service.domain.exception;

import org.springframework.http.HttpStatus;

public enum MessageType {
	
	TRAINER_NOT_FOUND("Trainer not found.", HttpStatus.NOT_FOUND),
    REPORT_SERVICE_TIMEOUT("Unable to reach report service.", HttpStatus.GATEWAY_TIMEOUT),
    INVALID_ARGUMENT("Invalid parameter provided.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("User authentication required.", HttpStatus.UNAUTHORIZED),
    GENERAL_ERROR("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
	
	private final String message;
	private final HttpStatus status;

	private MessageType(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
