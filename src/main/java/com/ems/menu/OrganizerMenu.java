/*
 * Author : Sowndariya
 * OrganizerMenu presents the organizer dashboard and routes
 * organizer inputs to appropriate actions for event creation,
 * ticket management, offer management, registration viewing,
 * and revenue reports.
 */
package com.ems.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ems.actions.OrganizerEventManagementAction;
import com.ems.actions.OrganizerOfferManagementAction;
import com.ems.actions.OrganizerTicketManagementAction;
import com.ems.actions.OrganizerRegistrationAction;
import com.ems.actions.OrganizerReportAction;
import com.ems.actions.NotificationAction;
import com.ems.actions.UserAction;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;

/*
 * Handles all organizer facing menu navigation.
 * Delegates actual business logic to action classes.
 */

public class OrganizerMenu extends BaseMenu {

    private final Scanner scanner;

    private final UserAction userAction;
    private final OrganizerEventManagementAction eventManagementAction;
    private final OrganizerTicketManagementAction ticketManagementAction;
    private final OrganizerRegistrationAction registrationAction;
    private final OrganizerReportAction reportAction;
    private final NotificationAction notificationAction;
    private final OrganizerOfferManagementAction offerManagementAction;

    public OrganizerMenu(Scanner scanner, User user) {
        super(user);
        this.scanner = scanner;
        
        this.userAction = new UserAction(ApplicationUtil.userService(), scanner);
        this.eventManagementAction = new OrganizerEventManagementAction(scanner);
        this.ticketManagementAction = new OrganizerTicketManagementAction(scanner);
        this.registrationAction = new OrganizerRegistrationAction(scanner);
        this.reportAction = new OrganizerReportAction();
        this.notificationAction = new NotificationAction();
        this.offerManagementAction = new OrganizerOfferManagementAction(scanner);
    }

    public void start() throws DataAccessException {

        notificationAction.displayUnreadNotifications(loggedInUser.getUserId());

        while (true) {
            System.out.println(
                    "\nOrganizer Menu\n" +
                    "1. Event management\n" +
                    "2. Ticket management\n" +
                    "3. Registrations\n" +
                    "4. Reports\n" +
                    "5. Notifications\n" +
                    "6. Offer management\n" +
                    "7. Update profile\n" +
                    "8. Logout\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    eventManagementMenu();
                    break;
                case 2:
                    ticketManagementMenu();
                    break;
                case 3:
                    registrationMenu();
                    break;
                case 4:
                    reportMenu();
                    break;
                case 5:
                    notificationMenu();
                    break;
                case 6:
                    offerManagementMenu();
                    break;
                case 7:
                    boolean updated = userAction.updateProfile(loggedInUser);
                    if (updated) return;
                    break;
                case 8:
                    if (confirmLogout()) return;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= EVENT MANAGEMENT ================= */

    private void eventManagementMenu() {

        while (true) {
            System.out.println(
                    "\nEvent Management\n" +
                    "1. Create event\n" +
                    "2. View my events\n" +
                    "3. Update event details\n" +
                    "4. Update event capacity\n" +
                    "5. Publish event\n" +
                    "6. Cancel event\n" +
                    "7. Back\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    eventManagementAction.createEvent(loggedInUser.getUserId());
                    break;
                case 2:
                    eventManagementAction.viewMyEventDetails(loggedInUser.getUserId());
                    break;
                case 3:
                    eventManagementAction.updateEventDetails(loggedInUser.getUserId());
                    break;
                case 4:
                    eventManagementAction.updateEventCapacity(loggedInUser.getUserId());
                    break;
                case 5:
                    eventManagementAction.publishEvent(loggedInUser.getUserId());
                    break;
                case 6:
                    eventManagementAction.cancelEvent(loggedInUser.getUserId());
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= TICKET MANAGEMENT ================= */

    private void ticketManagementMenu() {

        while (true) {
            System.out.println(
                    "\nTicket Management\n" +
                    "1. Update ticket price\n" +
                    "2. Update ticket quantity\n" +
                    "3. View ticket availability\n" +
                    "4. Back\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    ticketManagementAction.updateTicketPrice(loggedInUser.getUserId());
                    break;
                case 2:
                    ticketManagementAction.updateTicketQuantity(loggedInUser.getUserId());
                    break;
                case 3:
                    ticketManagementAction.viewTicketAvailability(loggedInUser.getUserId());
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= REGISTRATION ================= */

    private void registrationMenu() throws DataAccessException {

        while (true) {
            System.out.println(
                    "\nRegistrations\n" +
                    "1. View event registrations\n" +
                    "2. Back\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    registrationAction.viewEventRegistrations(loggedInUser.getUserId());
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= REPORTS ================= */

    private void reportMenu() {

        while (true) {
            System.out.println(
                    "\nReports\n" +
                    "1. Revenue summary\n" +
                    "2. Event summary\n" +
                    "3. Back\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    reportAction.getRevenueSummary(loggedInUser.getUserId());
                    break;
                case 2:
                    reportAction.getEventSummary(loggedInUser.getUserId());
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= NOTIFICATIONS ================= */

    private void notificationMenu() {

        while (true) {
            System.out.println(
                    "\nNotifications\n" +
                    "1 Send event update\n" +
                    "2 Send schedule change\n" +
                    "3 View notifications\n" +
                    "4 Back\n\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {

                case 1:
                case 2:
                    try {
                        List<Event> events =
                                eventManagementAction.getOrganizerEvents(loggedInUser.getUserId());

                        List<Event> validEvents = new ArrayList<>();

                        // ✅ FIXED FILTER (correct enum comparison)
                        for (Event e : events) {
                            if (e.getStatus() == EventStatus.PUBLISHED &&
                                    e.getStartDateTime().isAfter(DateTimeUtil.nowUtc())) {
                                validEvents.add(e);
                            }
                        }

                        if (validEvents.isEmpty()) {
                            System.out.println("No upcoming published events.");
                            break;
                        }

                        System.out.println("\nSelect Event:");
                        for (int i = 0; i < validEvents.size(); i++) {
                            System.out.println((i + 1) + ". " + validEvents.get(i).getTitle());
                        }

                        int eventChoice = InputValidationUtil.readInt(scanner,
                                "Enter choice: "
                        );

                        if (eventChoice < 1 || eventChoice > validEvents.size()) {
                            System.out.println("Invalid selection.");
                            break;
                        }

                        Event selected = validEvents.get(eventChoice - 1);

                        String msg = InputValidationUtil.readString(scanner,
                                "Enter message:\n"
                        );

                        if (choice == 1) {
                            notificationAction.sendEventUpdate(selected.getEventId(), msg);
                        } else {
                            notificationAction.sendScheduleChange(selected.getEventId(), msg);
                        }

                        System.out.println("Notification sent.");

                    } catch (DataAccessException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    notificationAction.displayAllNotifications(loggedInUser.getUserId());
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= OFFER ================= */

    private void offerManagementMenu() {

        while (true) {
            System.out.println(
                    "\nOffer Management\n" +
                    "1. Create offer\n" +
                    "2. View offers\n" +
                    "3. Activate offer\n" +
                    "4. Deactivate offer\n" +
                    "5. Back\n" +
                    "Choice:");

            int choice = InputValidationUtil.readInt(scanner, "");

            switch (choice) {
                case 1:
                    offerManagementAction.createOffer(loggedInUser.getUserId());
                    break;
                case 2:
                    offerManagementAction.viewAllOffers(loggedInUser.getUserId());
                    break;
                case 3:
                    offerManagementAction.activateOffer(loggedInUser.getUserId());
                    break;
                case 4:
                    offerManagementAction.deactivateOffer(loggedInUser.getUserId());
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /* ================= LOGOUT ================= */

    private boolean confirmLogout() {
        char choice = InputValidationUtil.readChar(
                scanner,
                "Are you sure you want to logout? (Y/N): "
        );
        return Character.toUpperCase(choice) == 'Y';
    }
}