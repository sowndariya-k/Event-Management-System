package com.ems.util;

import java.util.Scanner;

/*
 * Provides reusable console input helpers with built-in validation.
 *
 * Responsibilities:
 * - Safely read primitive and wrapper values from user input
 * - Enforce basic input constraints at the UI boundary
 * - Prevent invalid data from propagating into service and DAO layers
 *
 * Centralizes input handling to keep menu code clean and consistent.
 */
public final class InputValidationUtil {

	
	private InputValidationUtil() {
		
	}
    
	public static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

	public static double readDouble(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter a decimal number.");
                scanner.nextLine();
            }
        }
    }



	public static char readChar(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input.length() == 1) {
                return input.charAt(0);
            }
            System.out.println("Invalid input. Please enter a single character.");
        }
    }
	
	public static String readNonEmptyString(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    public static String readString(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
    
}