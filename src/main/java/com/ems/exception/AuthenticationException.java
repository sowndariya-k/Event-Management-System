package com.ems.exception;

/**
 * Thrown when user authentication fails
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AuthenticationException(String s) {
		super(s);
	}

}
