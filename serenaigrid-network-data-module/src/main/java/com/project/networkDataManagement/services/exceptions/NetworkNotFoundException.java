package com.project.networkDataManagement.services.exceptions;

public class NetworkNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NetworkNotFoundException() {
		super();
	}

	public NetworkNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NetworkNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkNotFoundException(String message) {
		super(message);
	}

	public NetworkNotFoundException(Throwable cause) {
		super(cause);
	}

}
