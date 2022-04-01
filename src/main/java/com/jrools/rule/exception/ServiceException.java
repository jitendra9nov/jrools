package com.jrools.rule.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = -7661881974219233311L;

	private final HttpStatus statusCode;
	
	public ServiceException (String message, HttpStatus statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}
}
