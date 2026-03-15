package com.ems.exception;

<<<<<<< HEAD
public class DataAccessException {

	

=======
/**
 * Wraps database and persistence layer errors
 */
public class DataAccessException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataAccessException(String s) {
		super(s);
	}
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433
}
