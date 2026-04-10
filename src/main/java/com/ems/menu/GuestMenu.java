/*
 * Author : Sowndariya
 * GuestMenu handles the menu flow for unauthenticated
 * guests, allowing them to browse events, register a new
 * account, or log in to access the full system.
 */
package com.ems.menu;

import java.util.Scanner;

import com.ems.actions.EventBrowsingAction;
import com.ems.actions.UserAction;
import com.ems.enums.UserRole;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

public class GuestMenu {

    private final Scanner scanner;
    private final UserAction userAction;
    private final EventBrowsingAction eventBrowsingAction;

    public GuestMenu(Scanner scanner) {
        this.scanner = scanner;

        this.userAction = new UserAction(ApplicationUtil.userService(), scanner);
        this.eventBrowsingAction =
                new EventBrowsingAction(scanner);
    }

    public void start() {

        System.out.println("Guest access provides limited features.");

        while (true) {
        	System.out.println(
					"\nGuest menu\n" +
							"1 Browse events\n" +
							"2 Register account\n" +
							"3 Exit guest mode\n\n" +
							"Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    browseEventsMenu();
                    break;

                case 2:
                    userAction.createAccount(UserRole.ATTENDEE);
                    break;

                case 3:
                    System.out.println("Returning to main menu...");
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void browseEventsMenu() {

        while (true) {
            System.out.println("\nBrowse Events");
            System.out.println("1. View all events");
            System.out.println("2. View event details");
            System.out.println("3. View ticket options");
            System.out.println("4. Back");
            System.out.print("Choice: ");

            int choice = InputValidationUtil.readInt(scanner, "");

            try {
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
                        return;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}