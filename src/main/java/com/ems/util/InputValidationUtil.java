package com.ems.util;

import java.util.Scanner;

public class InputValidationUtil {


	public static String readString(Scanner scanner, String message) {
		System.out.print(message);
	    return scanner.nextLine();
	}

	public static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
    }

	public static String readNonEmptyString(Scanner scanner, String message) {
		 String input;

		    while (true) {
		        System.out.print(message);
		        input = scanner.nextLine().trim();

		        if (!input.isEmpty()) {
		            return input;
		        }

		        System.out.println("Input cannot be empty. Try again.");
		    }
	}

	public static char readChar(Scanner scanner, String string) {
		 while (true) {
	            System.out.print(string);
	            String input = scanner.nextLine();
	            if (input.length() == 1) {
	                return input.charAt(0);
	            }
	            System.out.println("Invalid input. Please enter a single character.");
	        }
	}
	


	

}
