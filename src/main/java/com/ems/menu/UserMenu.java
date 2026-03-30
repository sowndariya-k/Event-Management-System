package com.ems.menu;

import java.util.Scanner;

import com.ems.actions.EventBrowsingAction;
import com.ems.actions.EventRegistrationAction;
import com.ems.actions.EventSearchAction;
import com.ems.actions.FeedbackAction;
import com.ems.actions.NotificationAction;
import com.ems.actions.UserAction;
import com.ems.actions.UserRegistrationAction;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.service.EventService;
import com.ems.service.OfferService;
import com.ems.util.InputValidationUtil;

public class UserMenu {

	private final Scanner scanner;
	private final User loggedInUser;

	private final NotificationAction notificationAction;
	private final EventBrowsingAction eventBrowsingAction;
	private final EventRegistrationAction eventRegistrationAction;
	private final UserRegistrationAction userRegistrationAction;
	private final EventSearchAction eventSearchAction;
	private final FeedbackAction feedbackAction;
	private final UserAction userAction;

	public UserMenu(Scanner scanner, User user, UserAction userAction, EventService eventService,
			OfferService offerService, UserRegistrationAction userRegistrationAction,
			EventRegistrationAction eventRegistrationAction) {

		this.scanner = scanner;
		this.loggedInUser = user;
		this.userAction = userAction;

		this.notificationAction = new NotificationAction();
		this.eventBrowsingAction = new EventBrowsingAction(scanner, eventService);
		this.eventRegistrationAction = eventRegistrationAction;
		this.userRegistrationAction = userRegistrationAction;
		this.eventSearchAction = new EventSearchAction(eventService, scanner);
		this.feedbackAction = new FeedbackAction();
	}

	public void start() throws DataAccessException {

		// show unread notifications once
		notificationAction.displayUnreadNotifications(loggedInUser.getUserId());

		while (true) {
			System.out.println("\nUser Menu\n" 
		            + "1. Browse events\n" 
					+ "2. Search and filter events\n"
					+ "3. My registrations\n" 
					+ "4. Notifications\n" 
					+ "5. Feedback\n" 
					+ "6. Update profile\n"
					+ "7. Logout\n\n" 
					+ "Choice:");

			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				browseEventsMenu();
				break;

			case 2:
				searchEventsMenu();
				break;

			case 3:
				registrationMenu();
				break;

			case 4:
				notificationAction.displayAllNotifications(loggedInUser.getUserId());
				break;

			case 5:
				feedbackMenu();
				break;

			case 6:
				boolean updated = userAction.updateProfile(loggedInUser);
				if (updated)
					return;
				break;

			case 7:
				if (confirmLogout()) {
					System.out.println("Logging out...");
					return;
				}
				break;

			default:
				System.out.println("Invalid choice. Try again.");
			}
		}
	}

	/* ===================== SUB MENUS ===================== */

	private void browseEventsMenu() throws DataAccessException {
		while (true) {
			System.out.println(""
					+ "\nBrowse Events\n" 
		            + "1. View all events\n" 
					+ "2. View event details\n"
					+ "3. View ticket options\n" 
					+ "4. Register for event\n" 
					+ "5. Back\n\n" 
					+ "Choice:");

			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				eventBrowsingAction.printAllAvailableEvents();
				break;

			case 2:
				eventBrowsingAction.viewEventDetails();
				break;

			case 3:
				eventBrowsingAction.viewTicketOptions();
				break;

			case 4:
				eventRegistrationAction.registerForAvailableEvent(loggedInUser.getUserId());
				break;

			case 5:
				return;

			default:
				System.out.println("Invalid choice. Try again.");
			}
		}
	}

	private void searchEventsMenu() {
		while (true) {
			System.out.println(""
					+ "\nSearch & Filter Events\n" 
					+ "1. Search by category\n" 
					+ "2. Search by date\n"
					+ "3. Search by date range\n" 
					+ "4. Search by city\n" 
					+ "5. Filter by price\n"
					+ "6. View all events\n" 
					+ "7. Back\n\n" 
					+ "Choice:");

			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				eventSearchAction.handleSearchByCategory();
				break;

			case 2:
				eventSearchAction.handleSearchByDate();
				break;

			case 3:
				eventSearchAction.handleSearchByDateRange();
				break;

			case 4:
				eventSearchAction.handleSearchByCity();
				break;

			case 5:
				eventSearchAction.handleFilterByPrice();
				break;

			case 6:
				eventBrowsingAction.printAllAvailableEvents();
				break;

			case 7:
				return;

			default:
				System.out.println("Invalid choice. Try again.");
			}
		}
	}

	private void registrationMenu() {
		while (true) {
			System.out.println(""
					+ "\nMy Registrations\n" 
					+ "1. Upcoming events\n" 
					+ "2. Past events\n"
					+ "3. Booking details\n" 
					+ "4. Cancel registration\n" 
					+ "5. Back\n\n" 
					+ "Choice:");

			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				userRegistrationAction.listUpcomingEvents(loggedInUser.getUserId());
				break;

			case 2:
				userRegistrationAction.listPastEvents(loggedInUser.getUserId());
				break;

			case 3:
				userRegistrationAction.viewBookingDetails(loggedInUser.getUserId());
				break;

			case 4:
				eventRegistrationAction.cancelRegistration(loggedInUser.getUserId());
				break;

			case 5:
				return;

			default:
				System.out.println("Invalid choice. Try again.");
			}
		}
	}

	private void feedbackMenu() {
		while (true) {
			System.out.println(""
					+ "\nFeedback\n" 
					+ "1. Submit rating\n" 
					+ "2. Back\n\n" 
					+ "Choice:");

			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				feedbackAction.submitRating(loggedInUser.getUserId(), scanner);
				break;

			case 2:
				return;

			default:
				System.out.println("Invalid choice. Try again.");
			}
		}
	}

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(scanner, "Are you sure you want to logout? (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}
}