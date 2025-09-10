package com.epam.gym.trainer_hours_service.domain.exception;

public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 745871451848100155L;

	private final ErrorMessage errorMessage;

	public BaseException(ErrorMessage errorMessage) {
		super(errorMessage.prepareErrorMessage());
		this.errorMessage = errorMessage;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

}
