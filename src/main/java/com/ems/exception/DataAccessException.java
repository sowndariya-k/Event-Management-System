package com.ems.exception;

/**
 * Wraps database and persistence layer errors
 */
public class DataAccessException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataAccessException(String s) {
		super(s);
	}
}
