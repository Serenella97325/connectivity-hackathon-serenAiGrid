package com.project.network.serenaigrid.networkManagement.services.exceptions;

public class NetworkServiceException extends RuntimeException {

	public NetworkServiceException() {
		super();
	}

	public NetworkServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NetworkServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkServiceException(String message) {
		super(message);
	}

	public NetworkServiceException(Throwable cause) {
		super(cause);
	}

}
