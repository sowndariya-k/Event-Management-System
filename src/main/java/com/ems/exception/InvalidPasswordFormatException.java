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
