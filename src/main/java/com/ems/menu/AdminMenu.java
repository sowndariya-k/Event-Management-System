/*
 * Author : Sowndariya
 * AdminMenu presents the admin dashboard menu and routes
 * admin inputs to the appropriate action classes for
 * managing users, events, venues, tickets, offers,
 * notifications, categories, and reports.
 */
package com.ems.menu;

import java.util.Scanner;

import com.ems.actions.AdminCategoryManagementAction;
import com.ems.actions.AdminEventManagementAction;
import com.ems.actions.AdminNotificationManagementAction;
import com.ems.actions.AdminOfferManagementAction;
import com.ems.actions.AdminReportAction;
import com.ems.actions.AdminTicketManagementAction;
import com.ems.actions.AdminUserManagementAction;
import com.ems.actions.AdminVenueManagementAction;
import com.ems.actions.NotificationAction;
import com.ems.actions.UserAction;
import com.ems.enums.NotificationType;
import com.ems.model.User;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

/*
 * Handles administrator related console interactions.
 *
 * Responsibilities:
 * - Display admin menus and navigation flows
 * - Collect and validate user input
 * - Delegate administrative operations to services
 */
public class AdminMenu extends BaseMenu {

	 private final Scanner scanner;

	    private final AdminUserManagementAction userManagementAction;
	    private final AdminEventManagementAction eventManagementAction;
	    private final AdminCategoryManagementAction categoryManagementAction;
	    private final AdminVenueManagementAction venueManagementAction;
	    private final AdminNotificationManagementAction notificationManagementAction;
	    private final AdminReportAction reportAction;
	    private final AdminOfferManagementAction offerManagementAction;
	    private final AdminTicketManagementAction ticketManagementAction;
	    private final NotificationAction notificationAction;
	    private final UserAction userAction;

	    public AdminMenu(Scanner scanner, User user) {
	        super(user);
	        this.scanner = scanner;
	        this.userManagementAction = new AdminUserManagementAction(scanner);
	        this.eventManagementAction = new AdminEventManagementAction(scanner);
	        this.categoryManagementAction = new AdminCategoryManagementAction(scanner);
	        this.venueManagementAction = new AdminVenueManagementAction(scanner);
	        this.notificationManagementAction = new AdminNotificationManagementAction(scanner);
	        this.reportAction = new AdminReportAction();
	        this.offerManagementAction = new AdminOfferManagementAction(scanner);
	        this.ticketManagementAction = new AdminTicketManagementAction(scanner);
	        this.notificationAction = new NotificationAction();
	        this.userAction = new UserAction(ApplicationUtil.userService(), scanner);
	    }

	public void start() {
		while (true) {
			eventManagementAction.markCompletedEvents();
			System.out.println(
				    "\nAdmin menu\n" +
				    "1 User management\n" +
				    "2 Event management\n" +
				    "3 Category management\n" +
				    "4 Venue management\n" +
				    "5 Ticket and registration management\n" +
				    "6 Offer and promotion management\n" +
				    "7 Reports and analytics\n" +
				    "8 Notifications\n" +
				    "9 Update profile\n" +
				    "10 Logout\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				userManagementMenu();
				break;
			case 2:
				eventManagementMenu();
				break;
			case 3:
				categoryManagementMenu();
				break;
			case 4:
				venueManagementMenu();
				break;
			case 5:
				ticketRegistrationManagementMenu();
				break;
			case 6:
				offerPromotionManagementMenu();
				break;
			case 7:
				reportsMenu();
				break;
			case 8:
				notificationMenu();
				break;
			case 9:
				boolean updated = userAction.updateProfile(loggedInUser);
				if(updated) {
					return;
				}
				break;
			case 10:
				eventManagementAction.markCompletedEvents();
				if (confirmLogout()) {
					System.out.println("Logging out...");
					return;
				}
				break;
			default:
				System.out.println("Invalid option. Please select a valid menu number.");
				break;
			}
		}
	}

	private void userManagementMenu() {
		while (true) {
			System.out.println(
				    "\nUser management\n" +
				    "1 View all users\n" +
				    "2 View organizers\n" +
				    "3 View admins\n" +
				    "4 Activate user\n" +
				    "5 Suspend user\n" +
				    "6 Back\n\n" +
				    "Choice:"
				);

			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {

			case 1: {
				userManagementAction.listUsersByRole("Attendee");
				break;
			}

			case 2: {
				userManagementAction.listUsersByRole("Organizer");
				break;
			}
			case 3: {
				userManagementAction.listUsersByRole("Admin");
				break;
			}
			case 4: {
				userManagementAction.changeUserStatus("ACTIVE");
				break;
			}

			case 5: {
				userManagementAction.changeUserStatus("SUSPENDED");
				break;
			}

			case 6: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}


	private void eventManagementMenu() {

		while (true) {
			System.out.println(
				    "\nEvent management\n" +
				    "1 View all events\n" +
				    "2 View available events\n"+
				    "3 View event details\n" +
				    "4 View ticket options\n" +
				    "5 Approve event\n" +
				    "6 Cancel event\n" +
				    "7 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {

			case 1: {
				eventManagementAction.listAllEvents();
				break;
			}
			
			case 2: {
				eventManagementAction.listAvailableEvents();
				break;
			}

			case 3: {
				eventManagementAction.printEventDetails();
				break;
			}

			case 4: {
				eventManagementAction.listTicketsForEvent();
				break;
			}

			case 5: {
				eventManagementAction.approveEvent(loggedInUser.getUserId());
				break;
			}

			case 6: {
				eventManagementAction.cancelEvent();
				break;
			}

			case 7: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private void reportsMenu() {
		while (true) {
			System.out.println(
				    "\nReports and analytics\n" +
				    "1 Event registrations\n" +
				    "2 Organizer performance\n" +
				    "3 Revenue report\n" +
				    "4 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				reportAction.viewEventWiseRegistrations();
				break;
			case 2:
				reportAction.viewOrganizerReport();
				break;
			case 3:
				reportAction.viewRevenueReport();
				break;
			case 4:
				return;
			default:
				System.out.println("Invalid option. Please select a valid menu number.");
				break;
			}
		}
	}

	private void notificationMenu() {

		while (true) {
			System.out.println(
				    "\nNotifications\n" +
				    "1 Send system update\n" +
				    "2 Send promotional message\n" +
				    "3 Notify user role\n" +
				    "4 Notify specific user\n" +
				    "5 View notifications\n" +
				    "6 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {

			case 1: {

				notificationManagementAction.sendSystemWideNotification(NotificationType.SYSTEM);
				break;
			}

			case 2: {
				notificationManagementAction.sendSystemWideNotification(NotificationType.EVENT);
				break;
			}

			case 3: {
				notificationManagementAction.sendNotificationByRole();
				break;
			}

			case 4: {
				notificationManagementAction.sendNotificationToSpecificUser();
				break;
			}

			case 5: {
				notificationAction.displayAllNotifications(loggedInUser.getUserId());
				break;
			}

			case 6: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private void categoryManagementMenu() {

		while (true) {
			System.out.println(
				    "\nCategory management\n" +
				    "1 View all categories\n" +
				    "2 Add category\n" +
				    "3 Update category\n" +
				    "4 Delete category\n" +
				    "5 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {

			case 1: {
				categoryManagementAction.listAllCategories();
				break;
			}

			case 2: {
				categoryManagementAction.addCategory();
				break;
			}

			case 3: {
				categoryManagementAction.updateCategory();
				break;
			}

			case 4: {
				categoryManagementAction.deleteCategory();
				break;
			}

			case 5: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	

	private void venueManagementMenu() {

		while (true) {
			System.out.println(
				    "\nVenue management\n" +
				    "1 View all venues\n" +
				    "2 Add venue\n" +
				    "3 Update venue details\n" +
				    "4 Remove venue\n" +
				    "5 View events by city\n" +
				    "6 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
				case 1: {
					venueManagementAction.listAllVenues();
					break;
				}
	
				case 2: {
					venueManagementAction.addVenue();
					break;
				}
				case 3: {
					venueManagementAction.updateVenue();
					break;
				}
	
				case 4: {
					venueManagementAction.removeVenue();
					break;
				}
	
				case 5: {
					venueManagementAction.listEventsByCity();
					break;
				}
	
				case 6: {
					return;
				}
	
				default: {
					System.out.println("Invalid option. Please select a valid menu number.");
				}
			}
		}
	}

	private void ticketRegistrationManagementMenu() {
		while (true) {
			System.out.println(
				    "\nTicket and registration management\n" +
				    "1 View tickets by event\n" +
				    "2 Ticket availability summary\n" +
				    "3 Registrations by event\n" +
				    "4 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {

			case 1: {
				ticketManagementAction.viewTicketsByEvent();
				break;
			}

			case 2: {
				ticketManagementAction.viewTicketSummary();
				break;
			}

			case 3: {
				ticketManagementAction.viewEventRegistrations();
				break;
			}

			case 4:
				return;

			default:
				System.out.println("Invalid option. Please select a valid menu number.");
			}
		}
	}

	

	private void offerPromotionManagementMenu() {
		while (true) {
			System.out.println(
				    "\nOffer and promotion management\n" +
				    "1 View offers\n" +
				    "2 Create offer\n" +
				    "3 Activate or deactivate offer\n" +
				    "4 Offer usage report\n" +
				    "5 Back\n\n" +
				    "Choice:"
				);


			int choice = InputValidationUtil.readInt(scanner, "");

			switch (choice) {
			case 1:
				offerManagementAction.viewAllOffers();
				break;
			case 2:
				offerManagementAction.createOffer();
				break;
			case 3:
				offerManagementAction.changeOfferStatus();
				break;

			case 4:
				offerManagementAction.viewOfferUsageReport();
				break;

			case 5:
				return;

			default:
				System.out.println("Invalid option. Please select a valid menu number.");
			}
		}
	}
	

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(scanner, "Are you sure you want to log out? (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}
}