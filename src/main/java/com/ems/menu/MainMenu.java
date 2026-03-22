package com.ems.menu;

import java.util.Scanner;

import com.ems.actions.EventRegistrationAction;
import com.ems.actions.UserAction;
import com.ems.actions.UserRegistrationAction;
import com.ems.dao.impl.RoleDaoImpl;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.service.impl.UserServiceImpl;
import com.ems.util.ApplicationUtil;
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
			System.out.println(
				    "\nMain menu\n" +
				    "1 Login\n" +
				    "2 Register as attendee\n" +
				    "3 Register as organizer\n" +
				    "4 Continue as guest\n" +
				    "5 Exit\n\n" +
				    "Choice:"
				);

			int input = InputValidationUtil.readInt(scanner, "");
			switch (input) {
			case 1:
				String email = InputValidationUtil.readString(scanner, "Enter the email address: ");
				String password = InputValidationUtil.readString(scanner, "Enter the password: ");

				try {
					User user = userService.login(email, password);
					if (user == null) {
						System.out.println("Login failed. Please try again.");
						break;
					}
					UserRole role = userService.getRole(user);

					switch (role) {
					case ADMIN:
						AdminMenu adminMenu = new AdminMenu(scanner, user);
						adminMenu.start();
						break;

					case ATTENDEE:
						UserMenu userMenu = new UserMenu(scanner,
								user,
								userAction,
								ApplicationUtil.eventService(),
								ApplicationUtil.offerService(),
								new UserRegistrationAction(ApplicationUtil.eventService(), scanner),
								new EventRegistrationAction(ApplicationUtil.eventService(), ApplicationUtil.offerService(), scanner));
						userMenu.start();
						break;

					case ORGANIZER:
						OrganizerMenu organizerMenu = new OrganizerMenu(scanner, user);
						organizerMenu.start();
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
				GuestMenu guestMenu = new GuestMenu(scanner);
				guestMenu.start();
				break;

			case 5:
				if (confirmLogout()) {
					System.out.println("Exiting the app...");
					return;
				}
				break;
			default:
				System.out.println("Invalid choice. Please select a number between 1 and 5.");
			}
		}
	}

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(scanner, "Are you sure you want to exit? (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}

}
