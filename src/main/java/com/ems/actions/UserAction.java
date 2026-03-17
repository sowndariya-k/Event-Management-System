package com.ems.actions;

import java.util.Scanner;


import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;

public class UserAction {
	private Scanner scanner;
	private final UserService userService;

	public UserAction(UserService userService, Scanner scanner) {
	    this.userService = userService;
	    this.scanner = scanner;
	}

	public User login() throws AuthorizationException, AuthenticationException {
	    String email =
	        InputValidationUtil.readNonEmptyString(
	            scanner,
	            "Enter email: "
	        );
	    while (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            email =
                InputValidationUtil.readNonEmptyString(
                    scanner,
                    "Enter valid Email Address: "
                );
        }

	    String password =
	        InputValidationUtil.readNonEmptyString(
	            scanner,
	            "Enter password: "
	        );

        return userService.login(email, password);
    }

    public UserRole getUserRole(User user) {
        return userService.getRole(user);
    }

	public void createAccount(UserRole role) {
	
		String fullName;
		do {
		    fullName = InputValidationUtil.readNonEmptyString(
		        scanner,
		        "Enter Full Name (max 30 chars): "
		    );
		    if (fullName.length()<2 ||fullName.length() >= 30) {
		        System.out.println("Error: Name must be minimum of 2 characters and a maximum of 30 characters.");
		    }
		} while (fullName.length() >= 30 || fullName.length()<2);

	
	    String email;
	    while (true) {
	        email = InputValidationUtil.readNonEmptyString(
	                scanner,
	                "Enter Email (max 100 characters): "
	            );

	        if (email.length() > 100) {
	        	System.out.println("Error: Email too long. Database limit is 100 characters.");
	        	continue;
	        	} 
	        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
	            System.out.println("Invalid email format.\nExample: name@company.com");
	            continue;
	        }
	        if (userExists(email)) {
	            System.out.println("This email is already registered.\nPlease try a different email.\n");
	            continue;
	        }
	
	        break;
	    }
	
	    String phone = InputValidationUtil.readString(
	    	    scanner,
	    	    "Enter phone number (optional): "
	    	);

	    	if (phone != null && !phone.trim().isEmpty()) {
	    	    phone = phone.replaceAll("\\D", "");

	    	    // starts with 6-9 and has exactly 10 digits
	    	    while (!phone.matches("^[6-9][0-9]{9}$")) {
	    	        phone = InputValidationUtil.readString(
	    	            scanner,
	    	            "Enter valid 10-digit phone number starting with 6-9: "
	    	        ).replaceAll("\\D", "");
	    	    }
	    	} else {
	    	    phone = null;
	    	}

	
	    	String passwordPrompt =
	    	        "Create a password:\n" +
	    	        "Minimum 8 characters\n" +
	    	        "At least 1 uppercase, 1 lowercase, 1 number, 1 special character\n";
	    	
	    	    String password = InputValidationUtil.readNonEmptyString(
	    	        scanner,
	    	        passwordPrompt
	    	    );
	    	
	    	    while (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
	    	        password = InputValidationUtil.readNonEmptyString(
	    	            scanner,
	    	            "Enter a valid password: "
	    	        );
	    	    }
	
	    int genderChoice;
	    do {
	        genderChoice = InputValidationUtil.readInt(
	             scanner,
	            "Enter your gender:\n1. Male\n2. Female\n3. Prefer not to say\n"
	        );
	    } while (genderChoice < 1 || genderChoice > 3);
	
	    String gender = (genderChoice == 1)
	        ? "Male"
	        : (genderChoice == 2)
	            ? "Female"
	            : "Opt-out";
	    boolean isUserCreated = userService.createAccount(fullName, email, phone, password, gender, role);
	    if(isUserCreated) {
	    	System.out.println("\nAccount has been created successfully!\n");
	    }else {
	    	System.out.println("\nAccount creation failed!, Retry");
	    }
	}
	
	 public boolean userExists(String email) {
	        return userService.checkUserExists(email);
	    }
	   
}