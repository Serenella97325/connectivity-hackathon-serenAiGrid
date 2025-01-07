package com.project.network.serenaigrid.networkManagement.services.exceptions;

public class NetworkNotFoundException extends RuntimeException {

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
