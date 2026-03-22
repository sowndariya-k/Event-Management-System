package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

public class AdminNotificationManagementAction {
	private final AdminService adminService;
    private final Scanner scanner;

    public AdminNotificationManagementAction(Scanner scanner) {
        this.adminService = ApplicationUtil.adminService();
        this.scanner = scanner;
    }

	public void sendSystemWideNotification(NotificationType type) {
		String message = InputValidationUtil.readNonEmptyString(scanner, "Enter message: ");
        try {
            adminService.sendSystemWideNotification(message, type);
            System.out.println("Notification sent successfully.");
        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
        }
		
	}
	
	private void sendNotificationByRole(String message, NotificationType type, UserRole role)
			throws DataAccessException {
		adminService.sendNotificationByRole(message, type, role);
	}

	private void sendNotificationToUser(String message, NotificationType type, int userId) throws DataAccessException {
		adminService.sendNotificationToUser(message, type, userId);
	}

	public void sendNotificationByRole() {
		try {
			System.out.println(
					"\nSelect user role\n" 
		            + "1. Attendee\n" 
		            + "2. Organizer\n");

			int roleChoice = InputValidationUtil.readInt(scanner, "");

			UserRole role;

			if (roleChoice == 1) {
				role = UserRole.ATTENDEE;
			} else if (roleChoice == 2) {
				role = UserRole.ORGANIZER;
			} else {
				System.out.println("Invalid role selected. Please try again.");
				return;
			}

			System.out.println(
					"\nSelect notification type\n" 
			        + "1. SYSTEM\n" 
				    + "2. EVENT\n");

			int typeChoice = InputValidationUtil.readInt(scanner, "");

			NotificationType type;

			if (typeChoice == 1) {
				type = NotificationType.SYSTEM;
			} else if (typeChoice == 2) {
				type = NotificationType.EVENT;
			} else {
				System.out.println("Invalid notification type selected.");
				return;
			}

			String message = InputValidationUtil.readNonEmptyString(scanner, "Enter message: ");

			sendNotificationByRole(message, type, role);

			System.out.println("Notification sent successfully.");
		} catch (DataAccessException e) {
			System.out.println("Error sending notification: " + e.getMessage());
		}
	}

	public void sendNotificationToSpecificUser() {
		try {
			List<User> users = adminService.getAllUsers();

			if (users.isEmpty()) {
				System.out.println("No users available");
				return;
			}
			  System.out.println("\nUsers:");
	            for (int i = 0; i < users.size(); i++) {
	                User u = users.get(i);
	                System.out.println((i + 1) + ". " + u.getFullName() + " (ID: " + u.getUserId() + ")");
	            }
		int choice = InputValidationUtil.readInt(scanner,
				"Select a user (1-" + users.size() + "): ");

		while (choice < 1 || choice > users.size()) {
			choice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
		}

		User selectedUser = users.get(choice - 1);

		System.out.println("\nSelect notification type\n" + "1. SYSTEM\n" + "2. EVENT\n" + "3. PAYMENT\n>");

		int typeChoice = InputValidationUtil.readInt(scanner, "");

		NotificationType type;

		if (typeChoice == 1) {
			type = NotificationType.SYSTEM;
		} else if (typeChoice == 2) {
			type = NotificationType.EVENT;
		} else if (typeChoice == 3) {
			type = NotificationType.PAYMENT;
		} else {
			System.out.println("Invalid notification type selected.");
			return;
		}

		String message = InputValidationUtil.readNonEmptyString(scanner, "Enter message: ");

		sendNotificationToUser(message, type, selectedUser.getUserId());

		System.out.println("Notification sent successfully.");
	} catch (DataAccessException e) {
		System.out.println("Error sending notification: " + e.getMessage());
	}
  }
		
}
