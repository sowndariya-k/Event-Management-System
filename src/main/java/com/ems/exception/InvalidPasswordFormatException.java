/*
 * Author : Sowndariya
 * InvalidPasswordFormatException is a custom checked exception
 * thrown when a user's password does not meet the required
 * format or strength policy during registration or update.
 */
package com.ems.exception;

/**
 * Thrown when password does not meet required format or policy
 */

public class InvalidPasswordFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPasswordFormatException(String s){
		super(s);
	}

}
