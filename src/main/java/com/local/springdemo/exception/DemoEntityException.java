package com.local.springdemo.exception;

public class DemoEntityException extends Exception {

	private static final long serialVersionUID = -5163852063186094910L;

	public DemoEntityException(String message) {
		super(message);
	}

	public DemoEntityException(String message, Throwable exception) {
		super(message, exception);
	}

}
