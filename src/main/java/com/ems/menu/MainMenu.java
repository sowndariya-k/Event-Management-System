package com.ems.menu;

import java.util.Scanner;

import com.ems.actions.UserAction;
import com.ems.dao.impl.RoleDaoImpl;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.service.impl.UserServiceImpl;
import com.ems.util.InputValidationUtil;

public class MainMenu {
	private Scanner scanner;
	private UserAction userAction;
	private UserService userService;

	public MainMenu(Scanner scanner) {

		this.scanner = scanner;

		UserDaoImpl userDao = new UserDaoImpl();
		RoleDaoImpl roleDao = new RoleDaoImpl();

		this.userService = new UserServiceImpl(userDao, roleDao);

		this.userAction = new UserAction(userService, scanner);

		start();
	}

	public void start() {
		 while (true) {
	            System.out.println("\nMain Menu"
	                    + "\n\nEnter your choice:"
	                    + "\n1. Login"
	                    + "\n2. Register as User"
	                    + "\n3. Register as Organizer"
	                    + "\n4. Continue as Guest"
	                    + "\nAny other number to Exit Application");

	            int input = InputValidationUtil.readInt(scanner, "");
	            switch (input) {
	                case 1:
	                    String email = InputValidationUtil.readString(scanner, "Enter the email address: ").trim().toLowerCase();
	                    String password = InputValidationUtil.readString(scanner, "Enter the password: ");

	                    try {
	                        User user = userService.login(email, password);
	                        if(user == null) {
	                            System.out.println("Login failed. Please try again.");
	                            break;
	                        }
	                        UserRole role = userService.getRole(user);

	                        switch (role) {
	                        case ADMIN:
	                            new AdminMenu(scanner, user);
	                            break;

	                        case ATTENDEE:
	                            new UserMenu(scanner, user);
	                            break;

	                        case ORGANIZER:
	                            new OrganizerMenu(scanner, user);
	                            break;

	                        default:
	                            System.out.println("Unexpected role. Cannot proceed.");
	                    }

	                    } catch (AuthorizationException | AuthenticationException | DataAccessException e) {
	                        System.out.println("Error: " + e.getMessage());
	                    }

	                    break;

	                case 2:
	                    userAction.createAccount(UserRole.ATTENDEE);
	                    break;

	                case 3:
	                    userAction.createAccount(UserRole.ORGANIZER);
	                    break;

	                case 4:
	                    new GuestMenu(scanner);
	                    break;

	                default:
	                    System.out.println("Thank you for using our event management system.");
	                    return;
			}
		}
	}

}