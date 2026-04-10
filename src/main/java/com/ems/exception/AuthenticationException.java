/*
 * Author : Sowndariya
 * AuthenticationException is a custom checked exception
 * thrown when a user login attempt fails due to invalid
 * credentials or unrecognized account details.
 */
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
