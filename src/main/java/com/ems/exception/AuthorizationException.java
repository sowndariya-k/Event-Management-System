/*
 * Author : Sowndariya
 * AuthorizationException is a custom checked exception
 * thrown when a user attempts to perform an action they
 * do not have permission to execute based on their role.
 */
package com.ems.exception;

/**
 * Thrown when a user lacks permission to perform an action
 */
public class AuthorizationException extends Exception{

	private static final long serialVersionUID = 1L;

	public AuthorizationException(String s) {
		super(s);
	}

}
