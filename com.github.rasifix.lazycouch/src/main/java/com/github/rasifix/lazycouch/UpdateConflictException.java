package com.github.rasifix.lazycouch;

public class UpdateConflictException extends CouchException {

	private static final long serialVersionUID = 2188186580605565029L;

	public UpdateConflictException(String message) {
		super(message);
	}

	public UpdateConflictException(Throwable cause) {
		super(cause);
	}

	public UpdateConflictException(String message, Throwable cause) {
		super(message, cause);
	}

}
