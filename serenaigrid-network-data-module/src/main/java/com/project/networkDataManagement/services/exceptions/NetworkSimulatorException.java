package com.project.networkDataManagement.services.exceptions;

public class NetworkSimulatorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NetworkSimulatorException() {
		super();
	}

	public NetworkSimulatorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NetworkSimulatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkSimulatorException(String message) {
		super(message);
	}

	public NetworkSimulatorException(Throwable cause) {
		super(cause);
	}

}
