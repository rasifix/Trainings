package com.github.rasifix.lazycouch;

public class NotFoundException extends CouchException {

	private static final long serialVersionUID = -7264447907873638728L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
