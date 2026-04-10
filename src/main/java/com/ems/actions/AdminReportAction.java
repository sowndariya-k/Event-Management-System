/*
 * Author : Sowndariya
 * AdminReportAction generates system-wide reports for the
 * admin, including event registration summaries and revenue
 * analytics filtered by user role and event details.
 */

package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.enums.UserRole;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;

public class AdminReportAction {

    private final AdminService adminService;
    private final EventService eventService;
    private final OrganizerService organizerService;
    private final Scanner scanner = new Scanner(System.in);

    private static final int TABLE_WIDTH = 110;
    private static final String SEPARATOR = "=".repeat(TABLE_WIDTH);
    private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

    public AdminReportAction() {
        this.adminService = ApplicationUtil.adminService();
        this.eventService = ApplicationUtil.eventService();
        this.organizerService = ApplicationUtil.organizerService();
    }

    // ================= EVENT REGISTRATION =================

    public void viewEventWiseRegistrations() {
        try {
            List<Event> events = eventService.getAllEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("No events available.");
                return;
            }

            printEvents(events);

            int choice = selectFromList(events.size(), "Select an event");
            Event selectedEvent = events.get(choice - 1);

            List<EventRegistrationReport> reports =
                    adminService.getEventWiseRegistrations(selectedEvent.getEventId());

            if (reports == null || reports.isEmpty()) {
                System.out.println("No registrations found.");
                return;
            }

            AdminMenuHelper.printEventRegistrationReport(reports);

        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= ORGANIZER REPORT =================

    public void viewOrganizerReport() {
        try {
            List<User> users = adminService.getUsersList(UserRole.ORGANIZER.name());

            if (users == null || users.isEmpty()) {
                System.out.println("No organizers found.");
                return;
            }

            printUsers(users);

            int choice = selectFromList(users.size(), "Select an organizer");

            List<OrganizerEventSummary> summary =
                    organizerService.getOrganizerEventSummary(users.get(choice - 1).getUserId());

            if (summary == null || summary.isEmpty()) {
                System.out.println("No events found.");
                return;
            }

            AdminMenuHelper.printOrganizerEventSummary(summary);

        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= REVENUE REPORT =================

    public void viewRevenueReport() {
        try {
            List<EventRevenueReport> reports = adminService.getRevenueReport();

            if (reports == null || reports.isEmpty()) {
                System.out.println("No revenue data available.");
                return;
            }

            AdminMenuHelper.printEventRevenueReport(reports);

        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= DISPLAY METHODS=================

    private void printEvents(List<Event> events) {
        System.out.println("\nAVAILABLE EVENTS");
        System.out.println(SEPARATOR);

        System.out.printf("%-5s %-30s %-20s %-20s %-10s %-10s%n",
                "NO", "TITLE", "CATEGORY", "START DATE", "TICKETS", "STATUS");

        System.out.println(SUB_SEPARATOR);

        int i = 1;
        for (Event e : events) {
            try {
                String category = eventService.getCategory(e.getCategoryId()).getName();
                int tickets = eventService.getAvailableTickets(e.getEventId());
                String formattedDate = DateTimeUtil
						.formatForDisplay(e.getStartDateTime());

                System.out.printf("%-5d %-30s %-20s %-20s %-10d %-10s%n",
                        i++,
                        truncate(e.getTitle(), 29),
                        category,
                        formattedDate,
                        tickets,
                        e.getStatus());

            } catch (Exception ex) {
                System.out.printf("%-5d %-30s %-50s%n",
                        i++,
                        truncate(e.getTitle(), 29),
                        "[Error fetching details]");
            }
        }

        System.out.println(SEPARATOR);
    }

    private void printUsers(List<User> users) {
        System.out.println(SEPARATOR);

        System.out.printf("%-5s %-5s %-20s %-10s %-25s %-15s %-10s%n",
                "NO", "ID", "NAME", "GENDER", "EMAIL", "PHONE", "STATUS");

        System.out.println(SUB_SEPARATOR);

        int i = 1;
        for (User u : users) {
            System.out.printf("%-5d %-5d %-20s %-10s %-25s %-15s %-10s%n",
                    i++,
                    u.getUserId(),
                    truncate(u.getFullName(), 19),
                    u.getGender(),
                    truncate(u.getEmail(), 24),
                    u.getPhone() == null ? "-" : u.getPhone(),
                    u.getStatus());
        }

        System.out.println(SEPARATOR);
    }

    private int selectFromList(int max, String prompt) {
        int choice;

        while (true) {
            System.out.print(prompt + " (1-" + max + "): ");
            choice = scanner.nextInt();

            if (choice >= 1 && choice <= max) {
                return choice;
            }

            System.out.println("Invalid choice. Try again.");
        }
    }

    private String truncate(String value, int length) {
        if (value == null) return "";
        return value.length() <= length ? value : value.substring(0, length - 3) + "...";
    }
}