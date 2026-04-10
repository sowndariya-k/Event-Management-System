/*
 * Author : Sowndariya
 * DataAccessException is a custom checked exception that
 * wraps database and persistence layer errors, thrown when
 * JDBC operations fail during data retrieval or modification.
 */

package com.ems.exception;

/**
 * Wraps database and persistence layer errors
 */
public class DataAccessException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataAccessException(String s) {
		super(s);
	}
	
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
