package com.github.rasifix.lazycouch;

public class CouchException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CouchException(String message) {
		super(message);
	}

	public CouchException(Throwable cause) {
		super(cause);
	}

	public CouchException(String message, Throwable cause) {
		super(message, cause);
	}

}
