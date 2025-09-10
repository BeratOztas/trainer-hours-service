package com.epam.gym.trainer_hours_service.domain.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.epam.gym.trainer_hours_service.domain.exception.BaseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = { BaseException.class })
	public ResponseEntity<ApiError> handleBaseException(BaseException ex, WebRequest request) {
		logger.warn("Handling BaseException. Message: {}, Details: {}",
				ex.getErrorMessage().getMessageType().getMessage(), ex.getErrorMessage().getDetails());

		HttpStatus status = ex.getErrorMessage().getMessageType().getStatus();
		String path = request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI()
				: "N/A";

		ApiError apiError = new ApiError(status.value(), ex.getMessage(), path, getHostName());

		return new ResponseEntity<>(apiError, status);
	}

	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
			WebRequest request) {
		logger.warn("Handling validation exception: {}", ex.getMessage());

		String path = request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI()
				: "N/A";
		String validationMessage = ex.getBindingResult().getFieldError() != null
				? ex.getBindingResult().getFieldError().getDefaultMessage()
				: "Validation failed";

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), validationMessage, path, getHostName());

		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<ApiError> handleGeneralException(Exception ex, WebRequest request) {
		logger.error("An unexpected error occurred: ", ex);

		String path = request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI()
				: "N/A";

		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.",
				path, getHostName());

		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error("Unable to determine host name for error response.", e);
			return "unknown-host";
		}
	}
}