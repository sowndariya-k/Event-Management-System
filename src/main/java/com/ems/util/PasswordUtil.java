/*
 * Author : Sowndariya
 * PasswordUtil is a utility class that provides methods
 * for hashing plain-text passwords and verifying passwords
 * against stored hashes using the BCrypt algorithm for
 * secure credential management.
 */
package com.ems.util;

import com.ems.exception.InvalidPasswordFormatException;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
private PasswordUtil(){
		
	}
	public static String hashPassword(String plainPassword)  throws InvalidPasswordFormatException{
		if(!plainPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
			throw new InvalidPasswordFormatException("Invalid password format");
		}
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

	

}
