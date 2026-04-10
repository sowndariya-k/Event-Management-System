/*
 * Author : Sowndariya
 * AdminUserManagementAction provides admin controls to
 * manage user accounts, including viewing, suspending,
 * activating, and sorting users by role or status.
 */

package com.ems.actions;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.ems.enums.UserStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

public class AdminUserManagementAction {
	private Scanner scanner;
	private final AdminService adminService;

	public AdminUserManagementAction(Scanner scanner) {
		this.scanner = scanner;
		this.adminService = ApplicationUtil.adminService();
	}
	
	public List<User> getUsersByRole(String role) throws DataAccessException {
		return adminService.getUsersList(role);
	}

	public void listUsersByRole(String role) {
		try {
            List<User> users = getUsersByRole(role);

            if (users.isEmpty()) {
                System.out.println("No " + role + " found at the moment.");
                return;
            }

            users.sort(Comparator.comparing(User::getFullName));
            displayUsers(users);

        } catch (DataAccessException e) {
            System.out.println("Error listing " + role + "s: " + e.getMessage());
        }
	}
	public List<User> getAllUsers() throws DataAccessException {
		return adminService.getAllUsers();
	}

	public void changeUserStatus(String status) {
	    try {
	        List<User> users = getAllUsers();

	        if (users.isEmpty()) {
	            System.out.println("No users available");
	            return;
	        }

	        users.sort(Comparator.comparing(User::getFullName));
	        
	        displayUsers(users);

	        int choice = InputValidationUtil.readInt(scanner,
	                "Select a user (1-" + users.size() + "): "
	        );

	        while (choice < 1 || choice > users.size()) {
	            choice = InputValidationUtil.readInt(scanner,
	                    "Enter a valid choice: "
	            );
	        }

	        User selectedUser = users.get(choice - 1);

	        UserStatus newStatus;
	        try {
	            newStatus = UserStatus.valueOf(status.toUpperCase());
	        } catch (IllegalArgumentException e) {
	            System.out.println("Invalid status: " + status);
	            return;
	        }

	        if (newStatus == selectedUser.getStatus()) {
	            System.out.println(selectedUser.getFullName()
	                    + "'s status is already: " + newStatus);
	            return;
	        }

	        // Confirm action
	        char confirm = InputValidationUtil.readChar(scanner,
	                "Change status to " + newStatus + " for "
	                        + selectedUser.getFullName() + " (Y/N): "
	        );

	        if (confirm == 'Y' || confirm == 'y') {

	            boolean success = adminService.changeStatus(
	                    newStatus,
	                    selectedUser.getUserId()
	            );

	            if (success) {
	                System.out.println("User status updated successfully.");
	            } else {
	                System.out.println("Failed to update user status.");
	            }

	        } else {
	            System.out.println("Action cancelled.");
	        }

	    } catch (DataAccessException e) {
	        System.out.println("Error changing user status: " + e.getMessage());
	    }
	}
	
	 // -----------------------------
    // INLINE DISPLAY METHOD
    // -----------------------------
    private void displayUsers(List<User> users) {

        System.out.println("\n====================================================================================================");
        System.out.printf("%-5s %-5s %-20s %-10s %-25s %-15s %-10s%n",
                "NO", "ID", "NAME", "GENDER", "EMAIL", "PHONE", "STATUS");
        System.out.println("----------------------------------------------------------------------------------------------------");

        int index = 1;

        for (User user : users) {
            System.out.printf("%-5d %-5d %-20s %-10s %-25s %-15s %-10s%n",
                    index++,
                    user.getUserId(),
                    truncate(user.getFullName(), 19),
                    user.getGender(),
                    user.getEmail(),
                    user.getPhone() == null ? "-" : user.getPhone(),
                    user.getStatus());
        }

        System.out.println("====================================================================================================");
    }

    private String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max - 3) + "...";
    }
}
